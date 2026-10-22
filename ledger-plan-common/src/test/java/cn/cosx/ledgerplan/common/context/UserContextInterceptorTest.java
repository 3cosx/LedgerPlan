package cn.cosx.ledgerplan.common.context;

import cn.cosx.ledgerplan.common.exception.BizException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserContextInterceptorTest {

    private final UserContextInterceptor interceptor = new UserContextInterceptor();

    @AfterEach
    void clearContext() {
        UserContextHolder.clear();
    }

    @Test
    void parsesUserHeadersIntoThreadLocal() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserContextHeaders.USER_ID, "42");
        request.addHeader(UserContextHeaders.EMAIL, "user@example.com");
        request.addHeader(UserContextHeaders.ROLES, "NORMAL, MEMBER");
        request.addHeader(UserContextHeaders.PERMISSIONS, "bookkeeping:manage,plan:manage");

        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertThat(proceed).isTrue();
        UserContext context = UserContextHolder.getRequired();
        assertThat(context.userId()).isEqualTo(42L);
        assertThat(context.email()).isEqualTo("user@example.com");
        assertThat(context.roles()).containsExactlyInAnyOrder("NORMAL", "MEMBER");
        assertThat(context.permissions())
                .containsExactlyInAnyOrder("bookkeeping:manage", "plan:manage");
    }

    @Test
    void leavesContextEmptyWhenHeadersAreAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertThat(proceed).isTrue();
        assertThat(UserContextHolder.get()).isNull();
    }

    @Test
    void clearsContextAfterRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserContextHeaders.USER_ID, "42");
        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        interceptor.afterCompletion(request, new MockHttpServletResponse(), new Object(), null);

        assertThat(UserContextHolder.get()).isNull();
    }

    @Test
    void rejectsMalformedUserId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserContextHeaders.USER_ID, "not-a-number");

        assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("用户信息头格式错误");
    }

    @Test
    void exposesRoleAndPermissionChecks() {
        UserContext context = new UserContext(1L, "a@b.com", Set.of("ADMIN"),
                Set.of("agent:execute"));

        assertThat(context.hasRole("ADMIN")).isTrue();
        assertThat(context.hasRole("NORMAL")).isFalse();
        assertThat(context.hasPermission("agent:execute")).isTrue();
        assertThat(context.hasPermission("bookkeeping:manage")).isFalse();
    }
}
