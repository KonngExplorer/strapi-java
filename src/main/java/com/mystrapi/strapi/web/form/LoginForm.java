package com.mystrapi.strapi.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
public class LoginForm {

    @NotNull(message = "用户名 {strapi.jakarta.validation.constraints.NotNull.message}")
    @Size(min = 4, max = 20, message = "用户名 {strapi.jakarta.validation.constraints.Size.min.message}" +
            "-{strapi.jakarta.validation.constraints.Size.min.message}")
    private String username;

    @NotNull(message = "密码 {strapi.jakarta.validation.constraints.NotNull.message}")
    @Size(min = 6, max = 20, message = "密码 {strapi.jakarta.validation.constraints.Size.min.message}" +
            "-{strapi.jakarta.validation.constraints.Size.min.message}")
    private String password;

    private String verifyCodeToken;
    private String verifyCode;
}
