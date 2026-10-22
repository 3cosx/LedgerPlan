package cn.cosx.ledgerplan.common.context;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.common.web.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从网关写入的请求头解析用户信息，并放入当前线程的 {@link UserContextHolder}。
 *
 * <p>所有依赖 common 的 Servlet 模块都会通过自动配置获得该拦截器。</p>
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader(UserContextHeaders.USER_ID);
        if (!StringUtils.hasText(userId)) {
            return true;
        }
        try {
            UserContext context = new UserContext(
                    Long.valueOf(userId),
                    request.getHeader(UserContextHeaders.EMAIL),
                    split(request.getHeader(UserContextHeaders.ROLES)),
                    split(request.getHeader(UserContextHeaders.PERMISSIONS)));
            UserContextHolder.set(context);
            return true;
        } catch (NumberFormatException e) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "用户信息头格式错误");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContextHolder.clear();
    }

    private Set<String> split(String value) {
        if (!StringUtils.hasText(value)) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toUnmodifiableSet());
    }
}
