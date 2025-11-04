package com.oauth2.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author huangzhao
 * @date 2025/9/28
 */
@Data
public class EmailVerifyDTO {

    @NotBlank(message = "email is empty.")
    private String email;
}
