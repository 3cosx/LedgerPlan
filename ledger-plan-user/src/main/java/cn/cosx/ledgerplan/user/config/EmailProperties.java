package cn.cosx.ledgerplan.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ledger-plan.email")
public class EmailProperties {

    /**
     * log：验证码打印到日志；smtp：通过 SMTP 发送。
     */
    private String provider = "log";

    private String from = "no-reply@ledgerplan.local";

    private String subject = "LedgerPlan 登录验证码";

    private int codeLength = 6;

    private int expireMinutes = 5;

    private int resendIntervalSeconds = 60;

    private int maxAttempts = 5;

    private String host = "localhost";

    private int port = 25;

    private String username;

    private String password;

    private boolean ssl;

    private boolean starttls;
}
