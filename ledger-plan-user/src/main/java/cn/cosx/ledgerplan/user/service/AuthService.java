package cn.cosx.ledgerplan.user.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.config.AuthProperties;
import cn.cosx.ledgerplan.user.dto.EmailLoginRequest;
import cn.cosx.ledgerplan.user.dto.LoginVO;
import cn.cosx.ledgerplan.user.dto.SendCodeRequest;
import cn.cosx.ledgerplan.user.dto.UserVO;
import cn.cosx.ledgerplan.user.entity.User;
import cn.cosx.ledgerplan.user.service.auth.UserAuthority;
import cn.cosx.ledgerplan.user.service.auth.UserAuthorityPort;
import cn.cosx.ledgerplan.user.support.EmailNormalizer;
import cn.cosx.ledgerplan.user.support.SessionKeys;
import cn.cosx.ledgerplan.user.support.UserErrorCode;
import cn.cosx.ledgerplan.user.support.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 认证用例：验证码登录、登出、当前登录用户。
 *
 * <p>只依赖应用层端口 {@link UserAuthorityPort}，RBAC 的表结构和 Mapper
 * 由 acl 包中的防腐层适配器隔离。</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserInfoService userInfoService;
    private final EmailCodeService emailCodeService;
    private final UserAuthorityPort userAuthorityPort;
    private final AuthProperties authProperties;

    public void sendCode(SendCodeRequest request) {
        emailCodeService.sendCode(EmailNormalizer.normalize(request.email()));
    }

    public LoginVO login(EmailLoginRequest request) {
        String email = EmailNormalizer.normalize(request.email());
        emailCodeService.verifyAndConsume(email, request.code());

        User user = userInfoService.findByEmail(email);
        if (user == null) {
            user = userInfoService.register(email);
        }
        if (!UserStatus.ACTIVE.name().equals(user.getStatus())) {
            throw new BizException(UserErrorCode.ACCOUNT_DISABLED);
        }

        UserAuthority authority = userAuthorityPort.loadForLogin(user.getId());

        long timeout = Boolean.TRUE.equals(request.rememberMe())
                ? authProperties.getRememberMeTimeoutSeconds()
                : authProperties.getDefaultTimeoutSeconds();

        StpUtil.login(user.getId(), new SaLoginModel()
                .setTimeout(timeout)
                .setIsLastingCookie(false));
        userInfoService.touchLastLogin(user.getId());

        SaSession session = StpUtil.getSession();
        session.set(SessionKeys.USER_ID, user.getId());
        session.set(SessionKeys.EMAIL, user.getEmail());
        session.set(SessionKeys.NICKNAME, user.getNickname());
        session.set(SessionKeys.ROLES, authority.roles());
        session.set(SessionKeys.PERMISSIONS, authority.permissions());
        session.set(SessionKeys.STATUS, user.getStatus());

        return new LoginVO(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                authority.roles(),
                authority.permissions(),
                StpUtil.getTokenValue(),
                timeout
        );
    }

    public void logout() {
        StpUtil.logout();
    }

    public UserVO currentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userInfoService.findById(userId);
        if (user == null) {
            StpUtil.logout();
            throw new BizException(UserErrorCode.USER_NOT_FOUND);
        }
        if (!UserStatus.ACTIVE.name().equals(user.getStatus())) {
            StpUtil.logout();
            throw new BizException(UserErrorCode.ACCOUNT_DISABLED);
        }
        UserAuthority authority = userAuthorityPort.load(user.getId());
        return UserVO.from(user, authority.roles(), authority.permissions());
    }
}
