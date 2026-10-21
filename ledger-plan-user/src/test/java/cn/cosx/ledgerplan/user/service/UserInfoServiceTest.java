package cn.cosx.ledgerplan.user.service;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.dto.UpdateProfileRequest;
import cn.cosx.ledgerplan.user.entity.User;
import cn.cosx.ledgerplan.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserMapper userMapper;

    private UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        userInfoService = new UserInfoService(userMapper);
    }

    @Test
    void registerCreatesActiveUserWithEmailPrefixAsNickname() {
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User user = userInfoService.register("alice@example.com");

        assertEquals("alice@example.com", user.getEmail());
        assertEquals("alice", user.getNickname());
        assertEquals("ACTIVE", user.getStatus());
        verify(userMapper).insert(user);
    }

    @Test
    void registerTruncatesLongNickname() {
        when(userMapper.insert(any(User.class))).thenReturn(1);
        String email = "a".repeat(50) + "@example.com";

        User user = userInfoService.register(email);

        assertEquals(32, user.getNickname().length());
    }

    @Test
    void registerReturnsExistingUserOnDuplicateKey() {
        when(userMapper.insert(any(User.class))).thenThrow(new DuplicateKeyException("uk_t_user_email"));
        User existing = new User();
        existing.setId(99L);
        existing.setEmail("alice@example.com");
        when(userMapper.selectOne(any())).thenReturn(existing);

        User user = userInfoService.register("alice@example.com");

        assertSame(existing, user);
    }

    @Test
    void updateProfileRejectsBlankNickname() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("alice@example.com");
        existing.setNickname("alice");
        when(userMapper.selectById(1L)).thenReturn(existing);

        BizException exception = assertThrows(BizException.class,
                () -> userInfoService.updateProfile(1L, new UpdateProfileRequest("   ", null)));

        assertEquals(4003, exception.getCode().intValue());
    }

    @Test
    void updateProfileTrimsNicknameAndAvatar() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("alice@example.com");
        existing.setNickname("alice");
        when(userMapper.selectById(1L)).thenReturn(existing);

        userInfoService.updateProfile(1L, new UpdateProfileRequest("  Alice  ", "  avatar.png  "));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals("Alice", captor.getValue().getNickname());
        assertEquals("avatar.png", captor.getValue().getAvatar());
    }

    @Test
    void getRequiredThrowsWhenUserMissing() {
        when(userMapper.selectById(404L)).thenReturn(null);

        BizException exception = assertThrows(BizException.class, () -> userInfoService.getRequired(404L));

        assertEquals(4041, exception.getCode().intValue());
    }
}
