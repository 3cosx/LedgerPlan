package cn.cosx.ledgerplan.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * RBAC 只读联表查询。写操作使用各 BaseMapper。
 */
public interface RbacMapper {

    @Select("""
            SELECT r.code
            FROM t_role r
            JOIN t_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            ORDER BY r.id
            """)
    List<String> selectRoleCodes(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT p.code
            FROM t_permission p
            JOIN t_role_permission rp ON rp.permission_id = p.id
            JOIN t_user_role ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
            ORDER BY p.code
            """)
    List<String> selectPermissionCodes(@Param("userId") Long userId);
}
