package cn.cosx.ledgerplan.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.cosx.ledgerplan.common.web.Result;
import cn.cosx.ledgerplan.user.dto.UpdateProfileRequest;
import cn.cosx.ledgerplan.user.dto.UserVO;
import cn.cosx.ledgerplan.user.service.AuthService;
import cn.cosx.ledgerplan.user.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SaCheckLogin
public class UserController {

    private final AuthService authService;
    private final UserInfoService userInfoService;

    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.ok(authService.currentUser());
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        userInfoService.updateProfile(userId, request);
        return Result.ok(authService.currentUser());
    }
}
