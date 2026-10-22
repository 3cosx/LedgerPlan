package cn.cosx.ledgerplan.gateway.filter;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.cosx.ledgerplan.gateway.support.SessionKeys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 网关鉴权通过后，把当前用户信息写入内部请求头，供下游服务使用。
 *
 * <p>客户端传入的同名头会先被移除，避免伪造用户身份。下游服务应只信任来自网关的请求。</p>
 */
@Component
public class UserContextHeaderFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String EMAIL_HEADER = "X-User-Email";
    public static final String ROLES_HEADER = "X-User-Roles";
    public static final String PERMISSIONS_HEADER = "X-User-Permissions";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
        requestBuilder.headers(headers -> {
            headers.remove(USER_ID_HEADER);
            headers.remove(EMAIL_HEADER);
            headers.remove(ROLES_HEADER);
            headers.remove(PERMISSIONS_HEADER);

            if (!StpUtil.isLogin()) {
                return;
            }

            SaSession session = StpUtil.getSession(false);
            if (session == null) {
                return;
            }

            Object userId = session.get(SessionKeys.USER_ID);
            if (userId != null) {
                headers.set(USER_ID_HEADER, String.valueOf(userId));
            }
            String email = join(session.get(SessionKeys.EMAIL));
            if (!email.isEmpty()) {
                headers.set(EMAIL_HEADER, email);
            }
            String roles = join(session.get(SessionKeys.ROLES));
            if (!roles.isEmpty()) {
                headers.set(ROLES_HEADER, roles);
            }
            String permissions = join(session.get(SessionKeys.PERMISSIONS));
            if (!permissions.isEmpty()) {
                headers.set(PERMISSIONS_HEADER, permissions);
            }
        });

        ServerWebExchange mutatedExchange = exchange.mutate().request(requestBuilder.build()).build();
        return chain.filter(mutatedExchange);
    }

    private String join(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
        return String.valueOf(value);
    }

    @Override
    public int getOrder() {
        // 必须晚于 Sa-Token 的 WebFilter 设置上下文，早于路由过滤器。
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
