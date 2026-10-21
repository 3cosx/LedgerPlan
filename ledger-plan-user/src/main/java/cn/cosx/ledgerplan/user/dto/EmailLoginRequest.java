package cn.cosx.ledgerplan.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmailLoginRequest(
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱长度不能超过128个字符")
        String email,

        @NotBlank(message = "验证码不能为空")
        @Pattern(regexp = "\\d{4,8}", message = "验证码格式不正确")
        String code,

        Boolean rememberMe
) {
}
