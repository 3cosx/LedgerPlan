package cn.cosx.ledgerplan.user.service;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.config.EmailProperties;
import cn.cosx.ledgerplan.user.service.email.EmailCodeSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSendException;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private EmailCodeSender emailCodeSender;

    private EmailProperties properties;
    private EmailCodeService emailCodeService;

    @BeforeEach
    void setUp() {
        properties = new EmailProperties();
        properties.setCodeLength(6);
        properties.setExpireMinutes(5);
        properties.setResendIntervalSeconds(60);
        properties.setMaxAttempts(5);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        emailCodeService = new EmailCodeService(redisTemplate, emailCodeSender, properties);
    }

    @Test
    void sendCodeStoresCodeAndSendsMail() {
        when(valueOperations.setIfAbsent(eq("ledger-plan:auth:code:resend:test@example.com"),
                eq("1"), eq(Duration.ofSeconds(60)))).thenReturn(true);

        emailCodeService.sendCode("  Test@Example.com ");

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailCodeSender).sendLoginCode(eq("test@example.com"), codeCaptor.capture(), eq(5));
        assertEquals(6, codeCaptor.getValue().length());
        verify(valueOperations).set(eq("ledger-plan:auth:code:test@example.com"),
                eq(codeCaptor.getValue()), eq(Duration.ofMinutes(5)));
        verify(redisTemplate).delete("ledger-plan:auth:code:attempt:test@example.com");
    }

    @Test
    void sendCodeRejectsTooFrequentRequests() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(false);

        BizException exception = assertThrows(BizException.class,
                () -> emailCodeService.sendCode("test@example.com"));

        assertEquals(429, exception.getCode().intValue());
        verify(emailCodeSender, never()).sendLoginCode(anyString(), anyString(), any(Integer.class));
    }

    @Test
    void sendCodeRollsBackWhenMailFails() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(true);
        doThrow(new MailSendException("smtp down"))
                .when(emailCodeSender).sendLoginCode(anyString(), anyString(), any(Integer.class));

        BizException exception = assertThrows(BizException.class,
                () -> emailCodeService.sendCode("test@example.com"));

        assertEquals(5001, exception.getCode().intValue());
        verify(redisTemplate).delete(List.of(
                "ledger-plan:auth:code:test@example.com",
                "ledger-plan:auth:code:resend:test@example.com"));
    }

    @Test
    void verifyAndConsumeAcceptsCorrectCode() {
        when(valueOperations.get("ledger-plan:auth:code:test@example.com")).thenReturn("123456");
        when(valueOperations.increment("ledger-plan:auth:code:attempt:test@example.com")).thenReturn(1L);

        emailCodeService.verifyAndConsume("Test@Example.com", "123456");

        verify(redisTemplate).expire("ledger-plan:auth:code:attempt:test@example.com", Duration.ofMinutes(5));
        verify(redisTemplate).delete(List.of(
                "ledger-plan:auth:code:test@example.com",
                "ledger-plan:auth:code:attempt:test@example.com",
                "ledger-plan:auth:code:resend:test@example.com"));
    }

    @Test
    void verifyAndConsumeRejectsWrongCode() {
        when(valueOperations.get("ledger-plan:auth:code:test@example.com")).thenReturn("123456");
        when(valueOperations.increment("ledger-plan:auth:code:attempt:test@example.com")).thenReturn(1L);

        BizException exception = assertThrows(BizException.class,
                () -> emailCodeService.verifyAndConsume("test@example.com", "000000"));

        assertEquals(4001, exception.getCode().intValue());
    }

    @Test
    void verifyAndConsumeLocksAfterTooManyAttempts() {
        when(valueOperations.get("ledger-plan:auth:code:test@example.com")).thenReturn("123456");
        when(valueOperations.increment("ledger-plan:auth:code:attempt:test@example.com")).thenReturn(6L);

        BizException exception = assertThrows(BizException.class,
                () -> emailCodeService.verifyAndConsume("test@example.com", "123456"));

        assertEquals(4002, exception.getCode().intValue());
        verify(redisTemplate).delete(List.of(
                "ledger-plan:auth:code:test@example.com",
                "ledger-plan:auth:code:attempt:test@example.com"));
    }

    @Test
    void verifyAndConsumeRejectsExpiredCode() {
        when(valueOperations.get("ledger-plan:auth:code:test@example.com")).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> emailCodeService.verifyAndConsume("test@example.com", "123456"));

        assertEquals(4001, exception.getCode().intValue());
    }
}
