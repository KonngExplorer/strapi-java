package com.mystrapi.strapi.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码错误异常
 * @author tangqiang
 */
public class BadVerifyCodeAuthenticationException extends AuthenticationException {
    public BadVerifyCodeAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BadVerifyCodeAuthenticationException(String msg) {
        super(msg);
    }
}
