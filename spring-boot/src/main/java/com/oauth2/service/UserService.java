package com.oauth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oauth2.model.entity.User;
import com.oauth2.model.dto.EmailVerifyDTO;
import com.oauth2.model.vo.UserVO;
import com.oauth2.security.UserPrincipal;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    UserVO getUserInfo(UserPrincipal userPrincipal);

    Map<String, Object> genToken(EmailVerifyDTO dto);

    UserVO getSignToken(String uid);

    /**
     * 保存用户信息
     *
     * @param dto 用户信息
     */
    void saveUserInfo(User dto);

    User getUserOrRegisterUser(String email);
}