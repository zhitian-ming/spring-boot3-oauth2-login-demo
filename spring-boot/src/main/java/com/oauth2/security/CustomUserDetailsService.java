package com.oauth2.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oauth2.exception.CustomException;
import com.oauth2.exception.code.UserErrorCode;
import com.oauth2.mapper.UserMapper;
import com.oauth2.model.entity.User;
import com.oauth2.model.cost.RedisKey;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    private final RedissonClient redissonClient;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, email));
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_EXISTS);
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Integer id) {

        RBucket<UserDetails> bucket = redissonClient.getBucket(String.format(RedisKey.LOGIN_USER, id));
        if (bucket.isExists()) {
            return bucket.get();
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_EXISTS);
        }

        UserPrincipal principal = UserPrincipal.create(user);
        bucket.set(principal, Duration.ofHours(1));
        return principal;
    }
}