package cn.cosx.ledgerplan.business.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LedgerCreateRequest {

    @NotBlank(message = "账本名称不能为空")
    @Size(max = 64, message = "账本名称不能超过 64 个字符")
    private String ledgerName;

    @Size(max = 64, message = "图标不能超过 64 个字符")
    private String icon;

    @Size(max = 255, message = "描述不能超过 255 个字符")
    private String description;

    @Size(max = 16, message = "货币不能超过 16 个字符")
    private String currency;
}
