package cn.cosx.ledgerplan.user.dto;

import cn.cosx.ledgerplan.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserVO(
        String id,
        String email,
        String nickname,
        String avatar,
        List<String> roles,
        List<String> permissions,
        String status,
        LocalDateTime createTime,
        LocalDateTime lastLoginTime
) {

    public static UserVO from(User user, List<String> roles, List<String> permissions) {
        return new UserVO(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                user.getAvatar(),
                roles,
                permissions,
                user.getStatus(),
                user.getCreateTime(),
                user.getLastLoginTime()
        );
    }
}
