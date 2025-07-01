package com.employment.auth.service.impl;

import com.employment.auth.model.User;
import com.employment.auth.service.UserService;
import com.employment.auth.util.AuthConstant;
import com.employment.auth.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_PREFIX = "auth:user:";
    private static final String TOKEN_BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    public String login(String username, String password) {
        User user = getUserByUsername(username);
        
        if (user == null) {
            return null;
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        
        // 生成token
            String token = jwtTokenUtil.generateToken(user);
            
        // 将用户信息存入Redis，方便后续使用
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, user, jwtExpiration, TimeUnit.MILLISECONDS);
        
        // 更新用户最后登录时间
        user.setUpdateTime(new Date());
        updateUser(user);
            
            return token;
    }

    @Override
    public User getUserByToken(String token) {
        if (!validateToken(token)) {
            return null;
        }
        
        // 先从Redis获取
        String tokenKey = TOKEN_PREFIX + token;
        User user = (User) redisTemplate.opsForValue().get(tokenKey);
        
        // 如果Redis中没有，则从JWT中获取用户名，再从数据库查询
        if (user == null) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            user = getUserByUsername(username);
            
            // 如果找到了用户，则将其存入Redis
            if (user != null) {
                redisTemplate.opsForValue().set(tokenKey, user, 
                        jwtTokenUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis(), 
                        TimeUnit.MILLISECONDS);
            }
        }
        
        return user;
    }

    @Override
    public boolean validateToken(String token) {
        // 先检查令牌是否在黑名单中
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + jwtTokenUtil.getTokenId(token);
        Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
        
        if (Boolean.TRUE.equals(isBlacklisted)) {
            return false;
        }
        
        // 再验证令牌有效性
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return username != null && jwtTokenUtil.validateToken(token, username);
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
        
        // 将旧token加入黑名单
        addToBlacklist(token);
        
        // 生成新token
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public void logout(String token) {
        // 将token加入黑名单
        addToBlacklist(token);
        
        // 从Redis中删除token关联的用户信息
        String tokenKey = TOKEN_PREFIX + token;
        redisTemplate.delete(tokenKey);
    }

    private void addToBlacklist(String token) {
        try {
            // 计算token剩余有效时间
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();
            
            if (ttl > 0) {
                // 将token ID加入黑名单，过期时间与token一致
                String blacklistKey = TOKEN_BLACKLIST_PREFIX + jwtTokenUtil.getTokenId(token);
                redisTemplate.opsForValue().set(blacklistKey, true, ttl, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // 如果token已经无效，则忽略异常
        }
    }

    @Override
    @Transactional
    public Long syncUser(User user, String sourceSystem) {
        // 检查用户是否已存在
        User existingUser = getUserByUsername(user.getUsername());
            
        if (existingUser != null) {
                // 更新用户信息
            user.setId(existingUser.getId());
            user.setSourceSystem(sourceSystem);
            updateUser(user);
            return existingUser.getId();
        } else {
            // 创建新用户
            user.setSourceSystem(sourceSystem);
            return createUser(user);
        }
    }
    
    @Override
    @Transactional
    public Long createUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置创建时间
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        // 保存用户到数据库
        // 这里假设有一个UserRepository，实际项目中需要实现
        // userRepository.save(user);
        
        // 将用户信息缓存到Redis
        String userKey = USER_PREFIX + user.getUsername();
        redisTemplate.opsForValue().set(userKey, user);
        
        return user.getId();
    }
    
    @Override
    @Transactional
    public boolean updateUser(User user) {
        // 更新时间
        user.setUpdateTime(new Date());
        
        // 更新用户到数据库
        // 这里假设有一个UserRepository，实际项目中需要实现
        // userRepository.save(user);
        
        // 更新Redis缓存
        String userKey = USER_PREFIX + user.getUsername();
        redisTemplate.opsForValue().set(userKey, user);
        
        return true;
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
        
        // 先从Redis缓存中获取
        String userKey = USER_PREFIX + username;
        User user = (User) redisTemplate.opsForValue().get(userKey);
        
        if (user != null) {
            return user;
        }
        
        // 如果缓存中没有，则从数据库查询
        // 这里假设有一个UserRepository，实际项目中需要实现
        // user = userRepository.findByUsername(username);
        
        // 如果数据库中找到了，则缓存到Redis
        if (user != null) {
            redisTemplate.opsForValue().set(userKey, user);
        }
        
        // 模拟返回
        return null;
    }
    
    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        return updateUser(user);
    }
    
    @Override
    @Transactional
    public boolean updateUserType(Long userId, Integer userType) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
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