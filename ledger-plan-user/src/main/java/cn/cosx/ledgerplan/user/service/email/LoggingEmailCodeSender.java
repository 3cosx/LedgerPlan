package cn.cosx.ledgerplan.user.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 开发环境使用：验证码直接打印到日志，避免必须配置 SMTP。
 */
public class LoggingEmailCodeSender implements EmailCodeSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailCodeSender.class);

    @Override
    public void sendLoginCode(String email, String code, int expireMinutes) {
        log.info("【LedgerPlan】邮箱 {} 的登录验证码为 {}，{} 分钟内有效（开发模式，未真实发送）",
                email, code, expireMinutes);
    }
}
