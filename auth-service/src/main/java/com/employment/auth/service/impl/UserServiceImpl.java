package com.employment.auth.service.impl;

import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.AuthConstant;
import com.employment.auth.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        if (AuthConstant.SourceSystem.EMPLOYMENT.equals(sourceSystem)) {
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
    
    @Override
    public Long createUser(User user) {
        // 加密密码
        String encodedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encodedPassword);
        
        // 默认存储在MySQL中
        String sql = "INSERT INTO employment_user (username, password, email, phone, real_name, status, " +
                    "avatar, user_type, create_time, update_time, source_system, source_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 'auth-service', NULL)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        mysqlJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRealName());
            ps.setInt(6, user.getStatus() != null ? user.getStatus() : AuthConstant.UserStatus.ENABLED);
            ps.setString(7, user.getAvatar());
            ps.setInt(8, user.getUserType() != null ? user.getUserType() : AuthConstant.UserType.STUDENT);
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().longValue();
    }
    
    @Override
    public boolean updateUser(User user) {
        if (user.getId() == null) {
            return false;
        }
        
        // 查询用户来源系统
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            return false;
        }
        
        String sourceSystem = existingUser.getSourceSystem();
        
        // 根据来源系统选择数据库
        JdbcTemplate jdbcTemplate;
        String tableName;
        
        if (AuthConstant.SourceSystem.AI_INTERVIEW.equals(sourceSystem)) {
            jdbcTemplate = postgresqlJdbcTemplate;
            tableName = "interview_user";
        } else {
            jdbcTemplate = mysqlJdbcTemplate;
            tableName = "employment_user";
        }
        
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        List<Object> params = new ArrayList<>();
        
        if (user.getUsername() != null) {
            sqlBuilder.append("username = ?, ");
            params.add(user.getUsername());
        }
        
        if (user.getPassword() != null) {
            sqlBuilder.append("password = ?, ");
            // 加密密码
            params.add(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        
        if (user.getEmail() != null) {
            sqlBuilder.append("email = ?, ");
            params.add(user.getEmail());
        }
        
        if (user.getPhone() != null) {
            sqlBuilder.append("phone = ?, ");
            params.add(user.getPhone());
        }
        
        if (user.getRealName() != null) {
            sqlBuilder.append("real_name = ?, ");
            params.add(user.getRealName());
        }
        
        if (user.getStatus() != null) {
            sqlBuilder.append("status = ?, ");
            params.add(user.getStatus());
        }
        
        if (user.getAvatar() != null) {
            sqlBuilder.append("avatar = ?, ");
            params.add(user.getAvatar());
        }
        
        if (user.getUserType() != null) {
            sqlBuilder.append("user_type = ?, ");
            params.add(user.getUserType());
        }
        
        // 添加更新时间
        sqlBuilder.append("update_time = NOW() ");
        
        // 添加WHERE条件
        sqlBuilder.append("WHERE id = ?");
        params.add(user.getId());
        
        int rowsAffected = jdbcTemplate.update(sqlBuilder.toString(), params.toArray());
        
        // 如果更新成功且用户在Redis中有缓存，更新缓存
        if (rowsAffected > 0 && redisTemplate.hasKey(USER_PREFIX + user.getId())) {
            User updatedUser = getUserById(user.getId());
            redisTemplate.opsForValue().set(USER_PREFIX + user.getId(), updatedUser, TOKEN_EXPIRATION, TimeUnit.SECONDS);
        }
        
        return rowsAffected > 0;
    }
    
    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 先从Redis缓存中查询
        User cachedUser = (User) redisTemplate.opsForValue().get(USER_PREFIX + userId);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        // 从MySQL中查询
        try {
            String sql = "SELECT * FROM employment_user WHERE id = ?";
            User user = mysqlJdbcTemplate.queryForObject(sql, new Object[]{userId}, new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException e) {
            // 如果MySQL中没有，尝试从PostgreSQL查询
            try {
                String sql = "SELECT * FROM interview_user WHERE id = ?";
                User user = postgresqlJdbcTemplate.queryForObject(sql, new Object[]{userId}, new UserRowMapper());
                return user;
            } catch (EmptyResultDataAccessException ex) {
                return null;
            }
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        
        // 先从MySQL数据库查询
        User user = findUserFromMysql(username);
        
        // 如果MySQL中没找到，再从PostgreSQL查询
        if (user == null) {
            user = findUserFromPostgresql(username);
        }
        
        return user;
    }
    
    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return updateUser(user);
    }
    
    @Override
    public boolean updateUserType(Long userId, Integer userType) {
        User user = new User();
        user.setId(userId);
        user.setUserType(userType);
        return updateUser(user);
    }
    
    @Override
    public List<User> getUsersByType(Integer userType) {
        List<User> users = new ArrayList<>();
        
        // 从MySQL查询
        String mysqlSql = "SELECT * FROM employment_user WHERE user_type = ?";
        List<User> mysqlUsers = mysqlJdbcTemplate.query(mysqlSql, new Object[]{userType}, new UserRowMapper());
        if (mysqlUsers != null) {
            users.addAll(mysqlUsers);
        }
        
        // 从PostgreSQL查询
        String postgreSql = "SELECT * FROM interview_user WHERE user_type = ?";
        List<User> pgUsers = postgresqlJdbcTemplate.query(postgreSql, new Object[]{userType}, new UserRowMapper());
        if (pgUsers != null) {
            users.addAll(pgUsers);
        }
        
        return users;
    }
    
    @Override
    public boolean checkUserPermission(Long userId, Integer requiredUserType) {
        User user = getUserById(userId);
        if (user == null || user.getUserType() == null) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (user.getUserType() == AuthConstant.UserType.ADMIN) {
            return true;
        }
        
        // 检查用户类型是否匹配所需权限
        return user.getUserType().equals(requiredUserType);
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