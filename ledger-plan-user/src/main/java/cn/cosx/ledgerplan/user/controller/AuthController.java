package cn.cosx.ledgerplan.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.cosx.ledgerplan.common.tool.Result;
import cn.cosx.ledgerplan.user.dto.EmailLoginRequest;
import cn.cosx.ledgerplan.user.dto.LoginVO;
import cn.cosx.ledgerplan.user.dto.SendCodeRequest;
import cn.cosx.ledgerplan.user.dto.UserVO;
import cn.cosx.ledgerplan.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 发送邮箱登录验证码。首次登录会在验证码校验通过后自动注册。
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendCode(request);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody EmailLoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @SaCheckLogin
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    @SaCheckLogin
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(authService.currentUser());
    }
}
