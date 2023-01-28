package com.mystrapi.strapi.security;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.web.form.LoginForm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义认证逻辑
 *
 * @author tangqiang
 */
public class StrapiAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Boolean verifyCodeRequire;
    private final ObjectMapper objectMapper;
    private final ThreadLocal<LoginForm> loginFormThreadLocal = new ThreadLocal<>();

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private static final String CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";

    public StrapiAuthenticationFilter(Boolean verifyCodeRequire, ObjectMapper objectMapper) {
        this.verifyCodeRequire = verifyCodeRequire;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 验证是否ajax请求
        if (request.getHeader(CONTENT_TYPE) == null || !CONTENT_TYPE_JSON.equalsIgnoreCase(request.getHeader(CONTENT_TYPE))) {
            throw new AuthenticationServiceException("非ajax请求");
        }
        // 校验验证码
        if (BooleanUtil.isTrue(verifyCodeRequire)) {
            String verifyCodeToken = obtainVerifyCodeToken(request);
            String verifyCode = obtainVerifyCode(request);
            if (StrUtil.isNotBlank(verifyCodeToken) && StrUtil.isNotBlank(verifyCode)) {
                String truthCode = (String) request.getSession().getAttribute(verifyCodeToken);
                if (validateCode(truthCode, verifyCode)) {
                    // 验证码正确
                    return this.getAuthenticationManager().authenticate(obtainAuthentication(request));
                }
            } else {
                // 验证码错误
                throw new BadVerifyCodeAuthenticationException("验证码错误");
            }
        }
        return this.getAuthenticationManager().authenticate(obtainAuthentication(request));
    }

    /**
     * 必须重写该方法，否则并发session控制不生效 SessionManagementFilter#doFilter(HttpServletRequest, HttpServletResponse, FilterChain)
     */
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authResult);
        this.securityContextHolderStrategy.setContext(context);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
        }
        super.getRememberMeServices().loginSuccess(request, response, authResult);
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private boolean validateCode(String truthCode, String verifyCode) {
        return truthCode.equalsIgnoreCase(verifyCode);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return obtainLoginForm(request).getPassword();
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return obtainLoginForm(request).getUsername();
    }

    protected String obtainVerifyCodeToken(HttpServletRequest request) {
        return obtainLoginForm(request).getVerifyCodeToken();
    }

    protected String obtainVerifyCode(HttpServletRequest request) {
        return obtainLoginForm(request).getVerifyCode();
    }

    protected LoginForm obtainLoginForm(HttpServletRequest request) {
        if (loginFormThreadLocal != null && loginFormThreadLocal.get() != null) {
            return loginFormThreadLocal.get();
        }
        try (InputStream inputStream = request.getInputStream()) {
            LoginForm loginForm = objectMapper.readValue(inputStream, LoginForm.class);
            assert loginFormThreadLocal != null;
            loginFormThreadLocal.set(loginForm);
            return loginFormThreadLocal.get();
        } catch (IOException e) {
            throw new AuthenticationServiceException("参数解析异常", e);
        }
    }

    protected Authentication obtainAuthentication(HttpServletRequest request) {
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                password);
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        loginFormThreadLocal.remove();
        return authRequest;
    }


}
