package cn.cosx.ledgerplan.common.context;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 把 {@link UserContextInterceptor} 注册到所有 Servlet 模块。
 */
public class UserContextWebMvcConfigurer implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;

    public UserContextWebMvcConfigurer(UserContextInterceptor userContextInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/actuator/**")
                // 先让 Sa-Token 完成登录校验，再解析可信的用户上下文。
                .order(1);
    }
}
