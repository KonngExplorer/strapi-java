package com.mystrapi.strapi.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
public class LoginForm {

    @NotNull(message = "{jakarta.validation.constraints.NotNull.message}")
    private String username;
    @NotNull(message = "{jakarta.validation.constraints.NotNull.message}")
    private String password;
    private String verifyCodeToken;
    private String verifyCode;

}
