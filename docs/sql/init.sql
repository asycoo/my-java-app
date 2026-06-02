-- 租户学习项目初始化脚本
-- docker compose 首次启动 MySQL 时自动执行；已存在库可手动：docker exec -i tenant-demo-mysql mysql -uroot -proot tenant_demo < docs/sql/init.sql

DROP TABLE IF EXISTS usr_org_person;
DROP TABLE IF EXISTS usr_org_info;
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

CREATE TABLE IF NOT EXISTS usr_org_info (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    org_name        VARCHAR(200) NOT NULL COMMENT '组织名称',
    org_short_name  VARCHAR(100) NOT NULL DEFAULT '' COMMENT '组织简称',
    org_remark      VARCHAR(500) NOT NULL DEFAULT '' COMMENT '描述',
    org_status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=正常,0=停用',
    sys_tenant_code VARCHAR(64)  NOT NULL COMMENT '租户编码',
    r_add_by        BIGINT       NOT NULL DEFAULT 0 COMMENT '创建人',
    r_add_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    r_modified_by   BIGINT       NOT NULL DEFAULT 0 COMMENT '更新人',
    r_modified_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_tenant (sys_tenant_code),
    KEY idx_org_name (org_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织信息表';

CREATE TABLE IF NOT EXISTS usr_org_person (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    org_id          BIGINT       NOT NULL COMMENT '组织ID',
    real_name       VARCHAR(64)  NOT NULL COMMENT '姓名',
    mobile          VARCHAR(16)  NOT NULL DEFAULT '' COMMENT '手机号',
    email           VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '邮箱',
    person_status   TINYINT      NOT NULL DEFAULT 1 COMMENT '1=正常,0=停用',
    sys_tenant_code VARCHAR(64)  NOT NULL COMMENT '租户编码',
    r_add_by        BIGINT       NOT NULL DEFAULT 0,
    r_add_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    r_modified_by   BIGINT       NOT NULL DEFAULT 0,
    r_modified_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_tenant (sys_tenant_code),
    KEY idx_org_id (org_id),
    KEY idx_real_name (real_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织成员表';

INSERT INTO sys_tenant (code, name, status) VALUES
    ('tenant_a', '租户 A', 1),
    ('tenant_b', '租户 B', 1)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO sys_user (sys_tenant_code, username, password, nickname, role, status) VALUES
    ('tenant_a', 'admin', '$2b$10$JCbroEACDjWEgQN2CA/BzeqBu0hENLQaX9tbKZNaMjVp79RZNDkmC', '管理员A', 'ADMIN', 1),
    ('tenant_a', 'user1', '$2b$10$JCbroEACDjWEgQN2CA/BzeqBu0hENLQaX9tbKZNaMjVp79RZNDkmC', '普通用户A', 'USER', 1),
    ('tenant_b', 'admin', '$2b$10$JCbroEACDjWEgQN2CA/BzeqBu0hENLQaX9tbKZNaMjVp79RZNDkmC', '管理员B', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), role = VALUES(role);

INSERT INTO usr_org_info (org_name, org_short_name, org_remark, org_status, sys_tenant_code, r_add_by, r_modified_by) VALUES
    ('示例组织A', '组织A', 'tenant_a 种子数据', 1, 'tenant_a', 0, 0),
    ('示例组织B', '组织B', 'tenant_b 种子数据', 1, 'tenant_b', 0, 0);
