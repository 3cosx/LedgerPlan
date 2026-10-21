package cn.cosx.ledgerplan.user.service;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.config.EmailProperties;
import cn.cosx.ledgerplan.user.service.email.EmailCodeSender;
import cn.cosx.ledgerplan.user.support.EmailNormalizer;
import cn.cosx.ledgerplan.user.support.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private static final String CODE_KEY_PREFIX = "ledger-plan:auth:code:";
    private static final String RESEND_KEY_PREFIX = "ledger-plan:auth:code:resend:";
    private static final String ATTEMPT_KEY_PREFIX = "ledger-plan:auth:code:attempt:";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;
    private final EmailCodeSender emailCodeSender;
    private final EmailProperties emailProperties;

    /**
     * 发送登录验证码。同一邮箱在 resendIntervalSeconds 内只能发送一次。
     */
    public void sendCode(String email) {
        String normalized = EmailNormalizer.normalize(email);
        String resendKey = RESEND_KEY_PREFIX + normalized;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(resendKey, "1", Duration.ofSeconds(emailProperties.getResendIntervalSeconds()));
        if (!Boolean.TRUE.equals(acquired)) {
            throw new BizException(UserErrorCode.EMAIL_CODE_TOO_FREQUENT);
        }

        String codeKey = CODE_KEY_PREFIX + normalized;
        String attemptKey = ATTEMPT_KEY_PREFIX + normalized;
        String code = generateCode();
        try {
            redisTemplate.opsForValue().set(codeKey, code, Duration.ofMinutes(emailProperties.getExpireMinutes()));
            redisTemplate.delete(attemptKey);
            emailCodeSender.sendLoginCode(normalized, code, emailProperties.getExpireMinutes());
        } catch (RuntimeException ex) {
            redisTemplate.delete(List.of(codeKey, resendKey));
            throw new BizException(UserErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 校验并消费验证码。验证成功后验证码立即失效，不能重复使用。
     */
    public void verifyAndConsume(String email, String code) {
        String normalized = EmailNormalizer.normalize(email);
        String codeKey = CODE_KEY_PREFIX + normalized;
        String attemptKey = ATTEMPT_KEY_PREFIX + normalized;

        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (!StringUtils.hasText(storedCode) || !StringUtils.hasText(code)) {
            throw new BizException(UserErrorCode.EMAIL_CODE_INVALID);
        }

        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        long attemptCount = attempts == null ? 0L : attempts;
        if (attemptCount == 1L) {
            redisTemplate.expire(attemptKey, Duration.ofMinutes(emailProperties.getExpireMinutes()));
        }
        if (attemptCount > emailProperties.getMaxAttempts()) {
            redisTemplate.delete(List.of(codeKey, attemptKey));
            throw new BizException(UserErrorCode.EMAIL_CODE_ATTEMPTS_EXCEEDED);
        }
        if (!storedCode.equals(code.trim())) {
            throw new BizException(UserErrorCode.EMAIL_CODE_INVALID);
        }

        // 兼容 Redis 4.0 以下版本：这些版本没有 UNLINK 命令。
        redisTemplate.delete(List.of(codeKey, attemptKey, RESEND_KEY_PREFIX + normalized));
    }

    private String generateCode() {
        int length = Math.max(4, Math.min(emailProperties.getCodeLength(), 8));
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(RANDOM.nextInt(10));
        }
        return builder.toString();
    }
}
