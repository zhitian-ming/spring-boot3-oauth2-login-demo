package com.oauth2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth2.mapper.UserProviderMapper;
import com.oauth2.model.entity.UserProvider;
import com.oauth2.service.UserProviderService;
import org.springframework.stereotype.Service;

/**
* @author huangzhao
* @description 针对表【t_user_provider(用户第三方平台表)】的数据库操作Service实现
* @createDate 2025-10-11 13:45:11
*/
@Service
public class UserProviderServiceImpl extends ServiceImpl<UserProviderMapper, UserProvider>
    implements UserProviderService {

}




