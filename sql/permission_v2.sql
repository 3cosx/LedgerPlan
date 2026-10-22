-- 权限模型 v2：记账 / 计划规划 / Agent 执行
-- 适用于已经执行过旧版 schema.sql、存在旧 9 个权限点的数据库。
-- 执行后需要重新登录，让 SaSession 中的 permissions 重新计算。
USE `ledger_plan`;

START TRANSACTION;

-- 先清理旧的权限关联，再替换权限点；角色本身保留。
DELETE FROM `t_role_permission`;
DELETE FROM `t_permission`;

INSERT INTO `t_permission` (`id`, `code`, `name`, `description`)
VALUES (1, 'bookkeeping:manage', '记账', '记录收支、管理账户与分类'),
       (2, 'plan:manage', '计划规划', '预算、计划与报表分析'),
       (3, 'agent:execute', 'Agent 执行', '触发 Agent 执行任务');

-- 普通用户：记账
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (1, 1);

-- 会员：记账 + 计划规划
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (2, 1), (2, 2);

-- 管理员：全部权限
INSERT INTO `t_role_permission` (`role_id`, `permission_id`)
VALUES (3, 1), (3, 2), (3, 3);

COMMIT;
