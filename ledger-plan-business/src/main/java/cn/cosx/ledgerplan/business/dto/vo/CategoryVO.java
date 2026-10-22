package cn.cosx.ledgerplan.business.dto.vo;

import java.time.LocalDateTime;

/**
 * 收支分类响应。
 */
public record CategoryVO(
        Long id,
        Long ledgerId,
        Long parentId,
        String categoryName,
        String icon,
        Integer type,
        Integer isSystem,
        Integer sort,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
