-- PostgreSQL表结构（用于AI-Interview系统）
CREATE TABLE IF NOT EXISTS interview_user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  phone VARCHAR(20),
  real_name VARCHAR(50),
  status SMALLINT DEFAULT 1,
  avatar VARCHAR(255),
  user_type SMALLINT, -- 用户类型：0-学生，1-教师，2-管理员，3-企业HR
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  source_system VARCHAR(20),
  source_id BIGINT,
  CONSTRAINT uk_username UNIQUE (username)
); 