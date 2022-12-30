package com.mystrapi.strapi.form;

import lombok.Data;

/**
 * @author tangqiang
 */
@Data
public class LoginForm {

    private String username;
    private String password;
    private String verifyCodeToken;
    private String verifyCode;

}
