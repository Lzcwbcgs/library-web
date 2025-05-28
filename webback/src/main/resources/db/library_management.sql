-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role VARCHAR(20) NOT NULL COMMENT '角色(ROLE_READER/ROLE_ADMIN/ROLE_SUPER_ADMIN)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
    credit_score INT DEFAULT 100 COMMENT '信用分',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
    UNIQUE KEY uk_username (username)
) COMMENT '用户表';

-- 图书分类表
CREATE TABLE book_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类ID',
    sort INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)'
) COMMENT '图书分类表';

-- 图书信息表
CREATE TABLE book_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    isbn VARCHAR(20) NOT NULL COMMENT 'ISBN',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    description TEXT COMMENT '图书描述',
    cover_url VARCHAR(255) COMMENT '封面图片URL',
    location VARCHAR(50) COMMENT '馆藏位置',
    price DECIMAL(10,2) COMMENT '价格',
    total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可借库存',
    borrow_count INT NOT NULL DEFAULT 0 COMMENT '借阅次数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:下架,1:正常)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
    UNIQUE KEY uk_isbn (isbn)
) COMMENT '图书信息表';

-- 借阅记录表
CREATE TABLE borrow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_time DATETIME NOT NULL COMMENT '借阅时间',
    due_time DATETIME NOT NULL COMMENT '应还时间',
    return_time DATETIME COMMENT '实际归还时间',
    renew_count INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    status TINYINT NOT NULL COMMENT '状态(0:借阅中,1:已归还,2:逾期未还,3:逾期已还)',
    fine_amount DECIMAL(10,2) DEFAULT 0 COMMENT '罚款金额',
    fine_paid TINYINT DEFAULT 0 COMMENT '罚款是否已缴纳(0:未缴纳,1:已缴纳)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
    KEY idx_user_id (user_id),
    KEY idx_book_id (book_id),
    KEY idx_status (status)
) COMMENT '借阅记录表';

-- 系统配置表
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(50) NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除(0:未删除,1:已删除)',
    UNIQUE KEY uk_key (config_key)
) COMMENT '系统配置表';

-- 初始化系统配置数据
INSERT INTO sys_config (config_key, config_value, description) VALUES
('MAX_BORROW_COUNT', '5', '最大借阅数量'),
('MAX_BORROW_DAYS', '30', '最大借阅天数'),
('MAX_RENEW_COUNT', '1', '最大续借次数'),
('OVERDUE_FINE_PER_DAY', '0.5', '每日逾期罚款金额');

-- 初始化超级管理员账号
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$ow7aLlxk/uXvMsXxe1ROPu0WxwqUE0K3YZrXfVtaYkN8FrB5j6C7.', '超级管理员', 'ROLE_SUPER_ADMIN', 1);
-- 注意：这里的密码是"123456"的BCrypt加密结果，实际部署时请修改 