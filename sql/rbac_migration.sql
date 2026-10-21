-- 从旧的 t_user.role 单角色字段迁移到 RBAC 关联表。
-- 使用顺序：
--   1. 先执行 sql/schema.sql，创建 t_role/t_permission/t_user_role/t_role_permission 并写入种子数据；
--   2. 再执行本脚本，把旧角色映射为 NORMAL/MEMBER/ADMIN，并删除 t_user.role 列。
-- 新建数据库不需要执行本脚本。

USE `ledger_plan`;

DROP PROCEDURE IF EXISTS `migrate_legacy_user_role`;

DELIMITER $$
CREATE PROCEDURE `migrate_legacy_user_role`()
BEGIN
    IF EXISTS(SELECT 1
              FROM information_schema.columns
              WHERE table_schema = DATABASE()
                AND table_name = 't_user'
                AND column_name = 'role') THEN
        INSERT IGNORE INTO `t_user_role` (`user_id`, `role_id`)
        SELECT u.`id`, r.`id`
        FROM `t_user` u
                 JOIN `t_role` r ON r.`code` = CASE UPPER(COALESCE(u.`role`, 'USER'))
                                                     WHEN 'ADMIN' THEN 'ADMIN'
                                                     WHEN 'MEMBER' THEN 'MEMBER'
                                                     ELSE 'NORMAL'
                                                 END;

        -- 旧 role 为空或无法识别的用户，统一补成普通用户
        INSERT IGNORE INTO `t_user_role` (`user_id`, `role_id`)
        SELECT u.`id`, 1
        FROM `t_user` u
        WHERE NOT EXISTS(SELECT 1 FROM `t_user_role` ur WHERE ur.`user_id` = u.`id`);

        ALTER TABLE `t_user` DROP COLUMN `role`;
    END IF;
END$$
DELIMITER ;

CALL `migrate_legacy_user_role`();
DROP PROCEDURE IF EXISTS `migrate_legacy_user_role`;
