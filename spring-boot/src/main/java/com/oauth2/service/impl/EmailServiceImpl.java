package com.oauth2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oauth2.mapper.EmailTemplateMapper;
import com.oauth2.model.entity.EmailTemplate;
import com.oauth2.model.enums.EmailTemplateEnum;
import com.oauth2.service.EmailService;
import com.oauth2.model.cost.RedisKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Map;

/**
 * @author huangzhao
 * @date 2025/9/26
 */
@Slf4j
@Service
@AllArgsConstructor
public class EmailServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplate> implements EmailService {

    private final static String CHARSET = "UTF-8";

    private final RedissonClient redissonClient;


    @Override
    public String send(String recipientEmail, Map<String, Object> params, EmailAttachment attachment, String templateCode) {

        RMap<String, EmailTemplate> templateRMap = redissonClient.getMap(RedisKey.EMAIL_TEMPLATE_KEY);
        EmailTemplate template = templateRMap.computeIfAbsent(templateCode, key -> lambdaQuery()
                .eq(EmailTemplate::getCode, key)
                .eq(EmailTemplate::getStatus, EmailTemplateEnum.Status.ENABLE.getStatus())
                .one());
        if (template == null) {
            return "fail";
        }

        return send(recipientEmail, params, attachment, template);
    }

    @Override
    public String send(String recipientEmail, Map<String, Object> params, String templateCode) {
        return send(recipientEmail, params, null, templateCode);
    }

    private String send(String recipientEmail, Map<String, Object> params, EmailAttachment attachment, EmailTemplate template) {
        try {

            String templateText = template.getTemplateText();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = String.format("${%s}", entry.getKey());
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), CHARSET);
                templateText = templateText.replace(key, value);
            }

            HtmlEmail email = new HtmlEmail();
            email.setHostName(template.getHostName());
            log.info("发送邮件。{}", template.getHostName());
            email.setSslSmtpPort("465");
            email.setSmtpPort(465);
            email.setSSLOnConnect(true);
            email.setAuthentication(template.getEmail(), template.getPassword());
            email.setFrom(template.getEmail(), template.getUserName());
            email.setCharset(CHARSET);
            email.addTo(recipientEmail);
            email.setSubject(template.getSubject());
            email.setHtmlMsg(templateText);
            if (attachment != null) {
                email.attach(attachment);
            }

            return email.send();
        } catch (Exception e) {
            log.error("发送邮件失败。", e);
        }
        return "fail";
    }

}
