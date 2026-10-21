-- LedgerPlan 初始化脚本（MySQL 8.x）
-- 说明：user 服务首次使用邮箱验证码登录时会自动注册 t_user 记录；
--       RBAC 使用 t_user / t_role / t_permission 以及两张关联表；
--       生产环境建议由数据库变更工具管理，不要直接在生产库执行本脚本。

CREATE DATABASE IF NOT EXISTS `ledger_plan`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `ledger_plan`;

CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`              BIGINT       NOT NULL COMMENT '用户ID（雪花ID）',
    `email`           VARCHAR(128) NOT NULL COMMENT '登录邮箱（统一小写）',
    `nickname`        VARCHAR(32)  NOT NULL COMMENT '昵称',
    `avatar`          VARCHAR(512)          DEFAULT NULL COMMENT '头像地址',
    `status`          VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最近登录时间',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user`     BIGINT                DEFAULT NULL COMMENT '创建人',
    `update_user`     BIGINT                DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`),
    KEY `idx_user_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

CREATE TABLE IF NOT EXISTS `t_role`
(
    `id`          BIGINT      NOT NULL COMMENT '角色ID',
    `code`        VARCHAR(32) NOT NULL COMMENT '角色编码：NORMAL/MEMBER/ADMIN',
    `name`        VARCHAR(32) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(128)         DEFAULT NULL COMMENT '角色说明',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色表';

CREATE TABLE IF NOT EXISTS `t_permission`
(
    `id`          BIGINT      NOT NULL COMMENT '权限ID',
    `code`        VARCHAR(64) NOT NULL COMMENT '权限编码，如 ledger:read',
    `name`        VARCHAR(32) NOT NULL COMMENT '权限名称',
    `description` VARCHAR(128)         DEFAULT NULL COMMENT '权限说明',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='权限表';

CREATE TABLE IF NOT EXISTS `t_user_role`
(
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT   NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_user_role_role` (`role_id`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户-角色关联表';

CREATE TABLE IF NOT EXISTS `t_role_permission`
(
    `role_id`       BIGINT   NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT   NOT NULL COMMENT '权限ID',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`, `permission_id`),
    KEY `idx_role_permission_permission` (`permission_id`),
    CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色-权限关联表';

-- 角色：普通 / 会员 / 管理员
INSERT INTO `t_role` (`id`, `code`, `name`, `description`)
VALUES (1, 'NORMAL', '普通用户', '基础记账权限'),
       (2, 'MEMBER', '会员', '基础权限，并可使用报表导出与预算管理'),
       (3, 'ADMIN', '管理员', '系统全部权限')
ON DUPLICATE KEY UPDATE `name`       = VALUES(`name`),
                        `description` = VALUES(`description`);

-- 权限点
INSERT INTO `t_permission` (`id`, `code`, `name`, `description`)
VALUES (1, 'ledger:read', '查看账本', '查看账本、流水和分类'),
       (2, 'ledger:write', '编辑账本', '新增、修改、删除账本数据'),
       (3, 'account:read', '查看账户', '查看账户列表和余额'),
       (4, 'account:write', '编辑账户', '新增、修改、删除账户'),
       (5, 'report:read', '查看报表', '查看统计报表'),
       (6, 'report:export', '导出报表', '导出报表文件（会员及以上）'),
       (7, 'budget:manage', '预算管理', '创建和管理预算（会员及以上）'),
       (8, 'admin:manage', '系统管理', '管理员后台接口'),
       (9, 'user:manage', '用户管理', '管理用户及其角色')
ON DUPLICATE KEY UPDATE `name`       = VALUES(`name`),
                        `description` = VALUES(`description`);

-- 普通用户：基础记账与查看报表
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

-- 会员：普通用户权限 + 报表导出 + 预算管理
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7);

-- 管理员：全部权限
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8), (3, 9);
