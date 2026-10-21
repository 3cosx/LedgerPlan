package cn.cosx.ledgerplan.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 32, message = "昵称长度不能超过32个字符")
        String nickname,

        @Size(max = 512, message = "头像地址长度不能超过512个字符")
        String avatar
) {
}
