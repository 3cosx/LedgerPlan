package cn.cosx.ledgerplan.user.dto;

import java.util.List;

public record LoginVO(
        String userId,
        String email,
        String nickname,
        List<String> roles,
        List<String> permissions,
        String token,
        long tokenTimeout
) {
}
