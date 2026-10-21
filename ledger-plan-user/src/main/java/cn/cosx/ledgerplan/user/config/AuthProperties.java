package cn.cosx.ledgerplan.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ledger-plan.auth")
public class AuthProperties {

    /**
     * 默认登录有效期：7 天。
     */
    private long defaultTimeoutSeconds = 604800L;

    /**
     * 勾选“记住我”后的有效期：30 天。
     */
    private long rememberMeTimeoutSeconds = 2592000L;
}
