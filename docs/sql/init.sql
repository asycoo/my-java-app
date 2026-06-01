-- 租户学习项目初始化脚本（阶段2起）
-- docker compose up -d 第一次起 MySQL docker-compose.yml 把该文件挂到容器的 docker-entrypoint-initdb.d/，只在数据卷为空时自动执行一次。
-- 开发环境可重复执行：先删后建
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_tenant;

CREATE TABLE IF NOT EXISTS sys_tenant (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL COMMENT '租户编码',
    name            VARCHAR(128) NOT NULL COMMENT '租户名称',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用',
    r_add_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户主数据';

CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sys_tenant_code VARCHAR(64)  NOT NULL COMMENT '所属租户',
    username        VARCHAR(64)  NOT NULL COMMENT '登录名',
    password        VARCHAR(128) NOT NULL COMMENT 'BCrypt 密文',
    nickname        VARCHAR(128)          COMMENT '显示名',
    role            VARCHAR(32)  NOT NULL DEFAULT 'ADMIN' COMMENT 'ADMIN|USER',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '1=正常',
    r_add_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_username (sys_tenant_code, username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户内登录账号';

INSERT INTO sys_tenant (code, name, status) VALUES
    ('tenant_a', '租户 A', 1),
    ('tenant_b', '租户 B', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 密码均为 123456（BCrypt）
INSERT INTO sys_user (sys_tenant_code, username, password, nickname, role, status) VALUES
    ('tenant_a', 'admin', '$2b$10$JCbroEACDjWEgQN2CA/BzeqBu0hENLQaX9tbKZNaMjVp79RZNDkmC', '管理员A', 'ADMIN', 1),
    ('tenant_b', 'admin', '$2b$10$JCbroEACDjWEgQN2CA/BzeqBu0hENLQaX9tbKZNaMjVp79RZNDkmC', '管理员B', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);
