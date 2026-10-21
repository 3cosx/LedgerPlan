package cn.cosx.ledgerplan.gateway.filter;

import cn.cosx.ledgerplan.gateway.support.GatewayResult;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 下游服务未启动、连不上或连接超时时，返回统一的 503 JSON，而不是默认的 500 HTML/堆栈。
 *
 * <p>该过滤器必须排在路由过滤器之前，才能包住 NettyRoutingFilter 抛出的连接异常。</p>
 */
@Component
public class DownstreamUnavailableFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DownstreamUnavailableFilter.class);

    private static final byte[] FALLBACK_BODY = ("{\"code\":503,\"success\":false,"
            + "\"message\":\"下游服务暂不可用\",\"data\":null}").getBytes(StandardCharsets.UTF_8);

    private final ObjectMapper objectMapper;

    public DownstreamUnavailableFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).onErrorResume(error -> {
            if (!isUnavailable(error) || exchange.getResponse().isCommitted()) {
                return Mono.error(error);
            }
            String path = exchange.getRequest().getPath().value();
            log.warn("Downstream service unavailable: {} {}", exchange.getRequest().getMethod(), path, error);
            return writeUnavailable(exchange.getResponse());
        });
    }

    private Mono<Void> writeUnavailable(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(toBody())));
    }

    private byte[] toBody() {
        try {
            return objectMapper.writeValueAsBytes(
                    GatewayResult.error(503, "下游服务暂不可用"));
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize downstream unavailable response", ex);
            return FALLBACK_BODY;
        }
    }

    private boolean isUnavailable(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof ConnectException
                    || current instanceof UnknownHostException
                    || current instanceof TimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
