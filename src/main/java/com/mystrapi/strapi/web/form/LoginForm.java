package com.mystrapi.strapi.web.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
public class LoginForm {

    @NotNull(message = "用户名 {strapi.jakarta.validation.constraints.NotNull.message}")
    @Min(value = 4, message = "用户名 {strapi.jakarta.validation.constraints.Min.message}")
    @Max(value = 20, message = "用户名 {strapi.jakarta.validation.constraints.Max.message}")
    private String username;

    @NotNull(message = "密码 {strapi.jakarta.validation.constraints.NotNull.message}")
    @Min(value = 6, message = "密码 {strapi.jakarta.validation.constraints.Min.message}")
    @Max(value = 20, message = "密码 {strapi.jakarta.validation.constraints.Max.message}")
    private String password;

    private String verifyCodeToken;
    private String verifyCode;
}
