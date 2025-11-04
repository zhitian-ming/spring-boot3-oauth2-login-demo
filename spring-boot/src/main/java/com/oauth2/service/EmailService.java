package com.oauth2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oauth2.model.entity.EmailTemplate;
import org.apache.commons.mail.EmailAttachment;

import java.util.Map;

/**
 * @author huangzhao
 * @date 2025/9/26
 */
public interface EmailService extends IService<EmailTemplate> {

    String send(String recipientEmail, Map<String, Object> params, EmailAttachment attachment, String templateCode);

    String send(String recipientEmail, Map<String, Object> params, String templateCode);
}
