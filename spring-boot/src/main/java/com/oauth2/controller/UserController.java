package com.oauth2.controller;

import com.oauth2.model.entity.RspEntity;
import com.oauth2.model.entity.User;
import com.oauth2.model.enums.EmailTemplateEnum;
import com.oauth2.security.CurrentUser;
import com.oauth2.security.TokenProvider;
import com.oauth2.security.UserPrincipal;
import com.oauth2.security.email.EmailCodeAuthenticationToken;
import com.oauth2.service.EmailService;
import com.oauth2.service.UserService;
import com.oauth2.model.dto.EmailVerifyDTO;
import com.oauth2.model.vo.UserVO;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/**
 * 用户控制器
 * @author huangzhao
 * @date 2025/9/23
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    private final RedissonClient redissonClient;

    private final TokenProvider tokenProvider;


    /**
     * 获取用户登录信息
     */
    @GetMapping("/info")
    public ResponseEntity<RspEntity<UserVO>> getUserInfo(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(new RspEntity<>(userService.getUserInfo(userPrincipal)));
    }

    /**
     * 发送验证邮件
     */
    @PostMapping("/email/sign/send")
    public ResponseEntity<RspEntity<?>> sendSignEmail(@RequestBody @Validated EmailVerifyDTO dto) {
        String email = dto.getEmail();
        Map<String, Object> params = userService.genToken(dto);
        emailService.send(email, params, EmailTemplateEnum.Code.LOGIN.getCode());
        return ResponseEntity.ok(new RspEntity<>(Map.of("uid", params.get("uid"))));
    }

    /**
     * 邮箱验证token验证
     */
    @PostMapping("/email/sign/verify")
    public ResponseEntity<RspEntity<UserVO>> signVerify(@RequestParam("uid") String uid, @RequestParam("token") String token) {
        // 创建认证token
        EmailCodeAuthenticationToken authRequest = new EmailCodeAuthenticationToken(uid, token);

        // 进行认证
        Authentication authentication = authenticationManager.authenticate(authRequest);

        // 认证成功，设置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成登录token
        String signToken = tokenProvider.createToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // 临时存储token
        redissonClient.getBucket(uid).set(signToken, Duration.ofMinutes(3));

        UserVO userVO = new UserVO();
        userVO.setId(userPrincipal.getId());
        userVO.setUsername(userPrincipal.getUsername());
        userVO.setToken(signToken);
        return ResponseEntity.ok(new RspEntity<>(userVO));
    }

    /**
     * 邮箱登录获取token
     */
    @GetMapping("/email/sign/getToken")
    public ResponseEntity<RspEntity<UserVO>> getSignToken(@RequestParam("uid") String uid) {
        return ResponseEntity.ok(new RspEntity<>(userService.getSignToken(uid)));
    }


    /**
     * 用户信息修改
     */
    @PostMapping("/save")
    public ResponseEntity<RspEntity<?>> userSave(@RequestBody User dto) {
        userService.saveUserInfo(dto);
        return ResponseEntity.ok(new RspEntity<>());
    }
}
