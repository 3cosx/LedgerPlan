package cn.cosx.ledgerplan.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.cosx.ledgerplan.gateway.support.AccountDisabledException;
import cn.cosx.ledgerplan.gateway.support.GatewayResult;
import cn.cosx.ledgerplan.gateway.support.SessionKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class GatewayAuthConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthConfiguration.class);

    private static final String FALLBACK_ERROR_JSON =
            "{\"code\":500,\"success\":false,\"message\":\"系统内部错误\",\"data\":null}";

    private static final String[] OPEN_PATHS = {
            "/auth/send-code",
            "/auth/login",
            "/actuator/health",
            "/actuator/health/**",
            "/error"
    };

    @Bean
    public SaReactorFilter saReactorFilter(ObjectMapper objectMapper) {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(OPEN_PATHS)
                .setAuth(obj -> {
                    ServerWebExchange exchange = SaReactorSyncHolder.getContext();
                    if (exchange != null && HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                        return;
                    }
                    StpUtil.checkLogin();

                    SaSession session = StpUtil.getSession();
                    Object status = session.get(SessionKeys.STATUS);
                    if (status != null && !"ACTIVE".equals(String.valueOf(status))) {
                        throw new AccountDisabledException();
                    }

                    String path = exchange == null ? "" : exchange.getRequest().getPath().value();
                    if (matches(path, "/admin")) {
                        StpUtil.checkPermission("admin:manage");
                    } else if (matches(path, "/report/export")) {
                        StpUtil.checkPermission("report:export");
                    } else if (matches(path, "/report")) {
                        StpUtil.checkPermission("report:read");
                    } else if (matches(path, "/budget")) {
                        StpUtil.checkPermission("budget:manage");
                    } else if (matches(path, "/ledger")
                            || matches(path, "/account")
                            || matches(path, "/transaction")
                            || matches(path, "/category")) {
                        StpUtil.checkPermission("ledger:read");
                    }
                })
                // Sa-Token 1.37 的 reactor filter 会把返回值 String.valueOf 后写入响应。
                // 这里只返回 JSON 字符串并自己设置状态码/Content-Type，避免二次写响应。
                .setError(e -> handleError(e, objectMapper));
    }

    private static boolean matches(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }

    private Object handleError(Throwable e, ObjectMapper objectMapper) {
        HttpStatus status;
        String message;

        if (e instanceof NotLoginException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "未登录或登录已过期";
        } else if (e instanceof AccountDisabledException) {
            status = HttpStatus.FORBIDDEN;
            message = "账号已被禁用";
        } else if (e instanceof NotPermissionException || e instanceof NotRoleException) {
            status = HttpStatus.FORBIDDEN;
            message = "没有操作权限";
        } else if (e instanceof SaTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "登录状态无效";
        } else {
            log.error("Gateway authentication failed", e);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "系统内部错误";
        }

        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
        if (exchange == null) {
            return FALLBACK_ERROR_JSON;
        }
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return "";
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            return objectMapper.writeValueAsString(GatewayResult.error(status.value(), message));
        } catch (JsonProcessingException ex) {
            log.error("Failed to write gateway error response", ex);
            return FALLBACK_ERROR_JSON;
        }
    }
}
