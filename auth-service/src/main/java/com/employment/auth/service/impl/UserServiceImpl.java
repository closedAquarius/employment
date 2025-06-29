package com.employment.auth.service.impl;

import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;

    @Autowired
    @Qualifier("postgresqlJdbcTemplate")
    private JdbcTemplate postgresqlJdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_PREFIX = "auth:user:";
    private static final long TOKEN_EXPIRATION = 24 * 60 * 60; // 24小时

    @Override
    public String login(String username, String password) {
        // 先从MySQL数据库查询
        User user = findUserFromMysql(username);
        
        // 如果MySQL中没找到，再从PostgreSQL查询
        if (user == null) {
            user = findUserFromPostgresql(username);
        }
        
        // 如果用户存在且密码匹配
        if (user != null && validatePassword(password, user.getPassword())) {
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(user);
            
            // 将用户信息存入Redis
            redisTemplate.opsForValue().set(TOKEN_PREFIX + token, user.getId(), TOKEN_EXPIRATION, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(USER_PREFIX + user.getId(), user, TOKEN_EXPIRATION, TimeUnit.SECONDS);
            
            return token;
        }
        
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            return null;
        }
        
        Object userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (userId == null) {
            return null;
        }
        
        return (User) redisTemplate.opsForValue().get(USER_PREFIX + userId);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token) && redisTemplate.hasKey(TOKEN_PREFIX + token);
    }

    @Override
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            return null;
        }
        
        User user = getUserByToken(token);
        if (user == null) {
            return null;
        }
        
        // 删除旧token
        redisTemplate.delete(TOKEN_PREFIX + token);
        
        // 生成新token
        String newToken = jwtTokenUtil.generateToken(user);
        redisTemplate.opsForValue().set(TOKEN_PREFIX + newToken, user.getId(), TOKEN_EXPIRATION, TimeUnit.SECONDS);
        
        // 更新用户缓存过期时间
        redisTemplate.expire(USER_PREFIX + user.getId(), TOKEN_EXPIRATION, TimeUnit.SECONDS);
        
        return newToken;
    }

    @Override
    public void logout(String token) {
        User user = getUserByToken(token);
        if (user != null) {
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.delete(USER_PREFIX + user.getId());
        }
    }

    @Override
    public Long syncUser(User user, String sourceSystem) {
        // 先查询用户是否已存在
        String sql;
        if ("employment".equals(sourceSystem)) {
            sql = "SELECT id FROM employment_user WHERE username = ? AND source_system = ?";
            List<Long> ids = mysqlJdbcTemplate.query(sql, new Object[]{user.getUsername(), sourceSystem}, 
                    (rs, rowNum) -> rs.getLong("id"));
            
            if (ids != null && !ids.isEmpty()) {
                // 更新用户信息
                sql = "UPDATE employment_user SET password = ?, email = ?, phone = ?, real_name = ?, " +
                      "status = ?, avatar = ?, user_type = ?, update_time = NOW(), source_id = ? " +
                      "WHERE id = ?";
                mysqlJdbcTemplate.update(sql, user.getPassword(), user.getEmail(), user.getPhone(), 
                        user.getRealName(), user.getStatus(), user.getAvatar(), user.getUserType(), 
                        user.getSourceId(), ids.get(0));
                return ids.get(0);
            } else {
                // 插入新用户
                sql = "INSERT INTO employment_user (username, password, email, phone, real_name, status, " +
                      "avatar, user_type, create_time, update_time, source_system, source_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";
                mysqlJdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), 
                        user.getPhone(), user.getRealName(), user.getStatus(), user.getAvatar(), 
                        user.getUserType(), sourceSystem, user.getSourceId());
                
                // 获取新插入的ID
                sql = "SELECT LAST_INSERT_ID()";
                return mysqlJdbcTemplate.queryForObject(sql, Long.class);
            }
        } else {
            // PostgreSQL处理逻辑
            sql = "SELECT id FROM interview_user WHERE username = ? AND source_system = ?";
            List<Long> ids = postgresqlJdbcTemplate.query(sql, new Object[]{user.getUsername(), sourceSystem}, 
                    (rs, rowNum) -> rs.getLong("id"));
            
            if (ids != null && !ids.isEmpty()) {
                // 更新用户信息
                sql = "UPDATE interview_user SET password = ?, email = ?, phone = ?, real_name = ?, " +
                      "status = ?, avatar = ?, user_type = ?, update_time = NOW(), source_id = ? " +
                      "WHERE id = ?";
                postgresqlJdbcTemplate.update(sql, user.getPassword(), user.getEmail(), user.getPhone(), 
                        user.getRealName(), user.getStatus(), user.getAvatar(), user.getUserType(), 
                        user.getSourceId(), ids.get(0));
                return ids.get(0);
            } else {
                // 插入新用户
                sql = "INSERT INTO interview_user (username, password, email, phone, real_name, status, " +
                      "avatar, user_type, create_time, update_time, source_system, source_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";
                postgresqlJdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), 
                        user.getPhone(), user.getRealName(), user.getStatus(), user.getAvatar(), 
                        user.getUserType(), sourceSystem, user.getSourceId());
                
                // 获取新插入的ID
                sql = "SELECT currval('interview_user_id_seq')";
                return postgresqlJdbcTemplate.queryForObject(sql, Long.class);
            }
        }
    }

    /**
     * 从MySQL数据库查询用户
     */
    private User findUserFromMysql(String username) {
        String sql = "SELECT * FROM employment_user WHERE username = ?";
        List<User> users = mysqlJdbcTemplate.query(sql, new Object[]{username}, new UserRowMapper());
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * 从PostgreSQL数据库查询用户
     */
    private User findUserFromPostgresql(String username) {
        String sql = "SELECT * FROM interview_user WHERE username = ?";
        List<User> users = postgresqlJdbcTemplate.query(sql, new Object[]{username}, new UserRowMapper());
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(String rawPassword, String encodedPassword) {
        // 这里使用MD5加密进行演示，实际应使用更安全的加密方式如BCrypt
        String encodedRawPassword = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        return encodedRawPassword.equals(encodedPassword);
    }

    /**
     * 用户行映射器
     */
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setRealName(rs.getString("real_name"));
            user.setStatus(rs.getInt("status"));
            user.setAvatar(rs.getString("avatar"));
            user.setUserType(rs.getInt("user_type"));
            user.setCreateTime(rs.getTimestamp("create_time"));
            user.setUpdateTime(rs.getTimestamp("update_time"));
            user.setSourceSystem(rs.getString("source_system"));
            user.setSourceId(rs.getLong("source_id"));
            return user;
        }
    }
} 