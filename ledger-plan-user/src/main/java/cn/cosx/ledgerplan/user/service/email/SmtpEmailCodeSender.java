package cn.cosx.ledgerplan.user.service.email;

import cn.cosx.ledgerplan.user.config.EmailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SmtpEmailCodeSender implements EmailCodeSender {

    private final JavaMailSender mailSender;
    private final EmailProperties properties;

    public SmtpEmailCodeSender(JavaMailSender mailSender, EmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void sendLoginCode(String email, String code, int expireMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getFrom());
        message.setTo(email);
        message.setSubject(properties.getSubject());
        message.setText("你的 LedgerPlan 登录验证码是：" + code + "，"
                + expireMinutes + " 分钟内有效。如非本人操作请忽略。");
        mailSender.send(message);
    }
}
