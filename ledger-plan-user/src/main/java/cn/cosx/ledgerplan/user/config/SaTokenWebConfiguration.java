package cn.cosx.ledgerplan.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 让 @SaCheckLogin / @SaCheckPermission 等注解在 user 服务内生效。
 * 即使请求绕过网关直接打到 user，也仍然需要登录。
 */
@Configuration
public class SaTokenWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}
