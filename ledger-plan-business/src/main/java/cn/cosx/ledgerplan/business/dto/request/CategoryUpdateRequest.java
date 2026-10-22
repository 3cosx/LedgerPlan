package cn.cosx.ledgerplan.business.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
        @Size(max = 64, message = "分类名称不能超过 64 个字符")
        String categoryName,

        @Size(max = 64, message = "图标不能超过 64 个字符")
        String icon,

        @Min(value = 0, message = "排序值不能小于 0")
        Integer sort) {
}
