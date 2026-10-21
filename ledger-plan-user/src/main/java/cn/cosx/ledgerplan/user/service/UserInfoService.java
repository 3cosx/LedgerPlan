package cn.cosx.ledgerplan.user.service;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.dto.UpdateProfileRequest;
import cn.cosx.ledgerplan.user.entity.User;
import cn.cosx.ledgerplan.user.mapper.UserMapper;
import cn.cosx.ledgerplan.user.support.UserErrorCode;
import cn.cosx.ledgerplan.user.support.UserStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserMapper userMapper;

    public User findByEmail(String email) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
    }

    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    public User getRequired(Long id) {
        User user = findById(id);
        if (user == null) {
            throw new BizException(UserErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 首次邮箱登录自动注册。依赖 t_user.email 的唯一索引处理并发注册。
     */
    public User register(String email) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(defaultNickname(email));
        user.setStatus(UserStatus.ACTIVE.name());
        try {
            userMapper.insert(user);
            return user;
        } catch (DuplicateKeyException ex) {
            User existing = findByEmail(email);
            if (existing == null) {
                throw ex;
            }
            return existing;
        }
    }

    public User updateProfile(Long id, UpdateProfileRequest request) {
        User user = getRequired(id);
        if (request.nickname() != null) {
            String nickname = request.nickname().trim();
            if (!StringUtils.hasText(nickname)) {
                throw new BizException(UserErrorCode.INVALID_NICKNAME);
            }
            user.setNickname(nickname);
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar().trim());
        }
        userMapper.updateById(user);
        return getRequired(id);
    }

    public void touchLastLogin(Long id) {
        User update = new User();
        update.setId(id);
        update.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(update);
    }

    private String defaultNickname(String email) {
        int atIndex = email.indexOf('@');
        String prefix = atIndex > 0 ? email.substring(0, atIndex).trim() : "";
        if (!StringUtils.hasText(prefix)) {
            return "用户";
        }
        return prefix.length() > 32 ? prefix.substring(0, 32) : prefix;
    }
}
