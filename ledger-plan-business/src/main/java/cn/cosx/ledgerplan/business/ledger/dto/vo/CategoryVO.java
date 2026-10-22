package cn.cosx.ledgerplan.business.ledger.dto.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收支分类响应。
 */
@Data
public class CategoryVO {

    private Long id;

    private Long ledgerId;

    private Long parentId;

    private String categoryName;

    private String icon;

    private Integer type;

    private Integer isSystem;

    private Integer sort;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
