package cn.cosx.ledgerplan.business.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreateRequest(
        @NotNull(message = "账本 ID 不能为空")
        Long ledgerId,

        @NotNull(message = "分类 ID 不能为空")
        Long categoryId,

        @NotNull(message = "支付渠道不能为空")
        @Min(value = 1, message = "支付渠道不合法")
        @Max(value = 5, message = "支付渠道不合法")
        Integer payChannel,

        @NotNull(message = "交易类型不能为空")
        @Min(value = 1, message = "交易类型只能是 1（收入）或 2（支出）")
        @Max(value = 2, message = "交易类型只能是 1（收入）或 2（支出）")
        Integer type,

        @NotNull(message = "金额不能为空")
        @DecimalMin(value = "0.01", message = "金额必须大于 0")
        @Digits(integer = 18, fraction = 2, message = "金额最多保留两位小数")
        BigDecimal amount,

        @NotNull(message = "交易时间不能为空")
        LocalDateTime transactionTime,

        @Size(max = 255, message = "备注不能超过 255 个字符")
        String remark,

        @Size(max = 255, message = "标签不能超过 255 个字符")
        String tags,

        @Size(max = 512, message = "附件地址不能超过 512 个字符")
        String attachUrl) {
}
