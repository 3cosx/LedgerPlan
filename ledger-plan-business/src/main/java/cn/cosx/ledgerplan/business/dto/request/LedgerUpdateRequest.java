package cn.cosx.ledgerplan.business.dto.request;

import jakarta.validation.constraints.Size;

public record LedgerUpdateRequest(
        @Size(max = 64, message = "账本名称不能超过 64 个字符")
        String ledgerName,

        @Size(max = 64, message = "图标不能超过 64 个字符")
        String icon,

        @Size(max = 255, message = "描述不能超过 255 个字符")
        String description,

        @Size(max = 16, message = "货币不能超过 16 个字符")
        String currency) {
}
