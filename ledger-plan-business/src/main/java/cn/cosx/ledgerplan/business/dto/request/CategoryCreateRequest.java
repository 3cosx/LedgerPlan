package cn.cosx.ledgerplan.business.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotNull(message = "账本 ID 不能为空")
        Long ledgerId,

        Long parentId,

        @NotBlank(message = "分类名称不能为空")
        @Size(max = 64, message = "分类名称不能超过 64 个字符")
        String categoryName,

        @Size(max = 64, message = "图标不能超过 64 个字符")
        String icon,

        @NotNull(message = "分类类型不能为空")
        @Min(value = 1, message = "分类类型只能是 1（收入）或 2（支出）")
        @Max(value = 2, message = "分类类型只能是 1（收入）或 2（支出）")
        Integer type,

        @Min(value = 0, message = "排序值不能小于 0")
        Integer sort) {
}
