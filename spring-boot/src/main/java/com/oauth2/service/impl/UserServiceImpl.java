package com.oauth2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth2.mapper.UserMapper;
import com.oauth2.model.entity.User;
import com.oauth2.model.enums.UserEnum;
import com.oauth2.exception.CustomException;
import com.oauth2.exception.code.UserErrorCode;
import com.oauth2.model.cost.RedisKey;
import com.oauth2.model.dto.EmailVerifyDTO;
import com.oauth2.model.vo.UserVO;
import com.oauth2.security.TokenProvider;
import com.oauth2.security.UserPrincipal;
import com.oauth2.service.UserService;
import com.oauth2.util.MD5Utils;
import com.oauth2.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedissonClient redissonClient;

    private final TokenProvider tokenProvider;

    @Override
    public UserVO getUserInfo(UserPrincipal userPrincipal) {
        User user = getById(userPrincipal.getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public Map<String, Object> genToken(EmailVerifyDTO dto) {

        var email = dto.getEmail();
        var limitKey = String.format(RedisKey.EMAIL_LIMIT_KEY, email);
        RBucket<String> limitBucket = redissonClient.getBucket(limitKey);
        if (!limitBucket.setIfAbsent("1", Duration.ofMinutes(2))) {
            throw new CustomException(UserErrorCode.EMAIL_SEND_LIMIT);
        }

        var uid = UUID.randomUUID().toString();
        var token = MD5Utils.MD5(email + UUID.randomUUID());
        var verifyKey = String.format(RedisKey.EMAIL_VERIFY_TOKEN_KEY, uid);
        RMap<String, String> map = redissonClient.getMap(verifyKey);
        map.put(token, email);
        map.expire(Duration.ofMinutes(2));

        return Map.of("uid", uid, "token", token);
    }

    @Override
    public UserVO getSignToken(String uid) {

        String token = redissonClient.<String>getBucket(uid).get();
        if (StringUtils.isBlank(token)) {
            throw new CustomException(UserErrorCode.VERIFY_TOKEN_ERROR);
        }

        Integer userId = tokenProvider.getUserIdFromToken(token);
        User user = getById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);
        return userVO;
    }

    @Override
    public void saveUserInfo(User dto) {
        dto.setId(UserUtils.getUserId());
        this.saveOrUpdate(dto);
    }

    @Override
    public User getUserOrRegisterUser(String email) {
        Optional<User> userOptional = lambdaQuery()
                .eq(User::getUsername, email)
                .oneOpt();

        User user = userOptional.orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setStatus(UserEnum.Status.ENABLE.getStatus());
            newUser.setCreatedTime(LocalDateTime.now());
            newUser.setModifiedTime(LocalDateTime.now());
            return newUser;
        });

        if (user.getStatus() != UserEnum.Status.ENABLE.getStatus()) {
            throw new CustomException(UserErrorCode.USER_DISABLE);
        }

        user.setLastLoginTime(LocalDateTime.now());
        saveOrUpdate(user);
        return user;
    }
}