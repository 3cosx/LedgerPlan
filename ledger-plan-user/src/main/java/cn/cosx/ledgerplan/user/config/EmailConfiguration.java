package cn.cosx.ledgerplan.user.config;

import cn.cosx.ledgerplan.user.service.email.EmailCodeSender;
import cn.cosx.ledgerplan.user.service.email.LoggingEmailCodeSender;
import cn.cosx.ledgerplan.user.service.email.SmtpEmailCodeSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class EmailConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "ledger-plan.email", name = "provider", havingValue = "smtp")
    public EmailCodeSender smtpEmailCodeSender(EmailProperties properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setDefaultEncoding("UTF-8");
        sender.setHost(properties.getHost());
        sender.setPort(properties.getPort());

        boolean hasUsername = StringUtils.hasText(properties.getUsername());
        sender.setUsername(hasUsername ? properties.getUsername() : null);
        sender.setPassword(StringUtils.hasText(properties.getPassword()) ? properties.getPassword() : null);

        Properties javaMailProperties = sender.getJavaMailProperties();
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.smtp.auth", String.valueOf(hasUsername));
        javaMailProperties.put("mail.smtp.starttls.enable", String.valueOf(properties.isStarttls()));
        javaMailProperties.put("mail.smtp.ssl.enable", String.valueOf(properties.isSsl()));
        javaMailProperties.put("mail.smtp.connectiontimeout", "5000");
        javaMailProperties.put("mail.smtp.timeout", "5000");
        javaMailProperties.put("mail.smtp.writetimeout", "5000");
        return new SmtpEmailCodeSender(sender, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ledger-plan.email", name = "provider", havingValue = "log", matchIfMissing = true)
    public EmailCodeSender loggingEmailCodeSender() {
        return new LoggingEmailCodeSender();
    }
}
