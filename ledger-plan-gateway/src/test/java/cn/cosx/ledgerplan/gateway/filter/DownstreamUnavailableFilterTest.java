package cn.cosx.ledgerplan.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DownstreamUnavailableFilterTest {

    private final DownstreamUnavailableFilter filter =
            new DownstreamUnavailableFilter(new ObjectMapper());

    @Test
    void connectExceptionBecomes503Json() throws Exception {
        MockServerWebExchange exchange = exchange("/transaction/foo");

        filter.filter(exchange, ex -> Mono.error(new ConnectException("Connection refused"))).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
        String body = exchange.getResponse().getBodyAsString().block();
        assertTrue(body != null && body.contains("\"code\":503"));
    }

    @Test
    void unknownHostBecomes503Json() {
        MockServerWebExchange exchange = exchange("/account/list");

        filter.filter(exchange, ex -> Mono.error(new UnknownHostException("ledger-plan-business"))).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    @Test
    void otherErrorsAreRethrown() {
        MockServerWebExchange exchange = exchange("/ledger/list");

        assertThrows(IllegalStateException.class,
                () -> filter.filter(exchange, ex -> Mono.error(new IllegalStateException("boom"))).block());
        assertEquals(null, exchange.getResponse().getStatusCode());
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }
}
