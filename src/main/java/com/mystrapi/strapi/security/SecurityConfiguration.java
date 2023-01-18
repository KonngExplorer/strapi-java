package com.mystrapi.strapi.security;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.bs.bo.AuthorityBO;
import com.mystrapi.strapi.bs.bo.GroupBO;
import com.mystrapi.strapi.bs.bo.UserBO;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.Group;
import com.mystrapi.strapi.web.view.ViewResult;
import com.mystrapi.strapi.web.view.login.LoginView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 安全配置
 *
 * @author tangqiang
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    @Value("${strapi.login.verifyCodeRequire:false}")
    private Boolean verifyCodeRequire;

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http, ObjectMapper objectMapper, StrapiAuthorizationManager strapiAuthorizationManager) throws Exception {
        StrapiAuthenticationFilter strapiAuthenticationFilter = getStrapiAuthenticationFilter(objectMapper);
        SecurityFilterChain securityFilterChain = http.csrf().disable().cors().disable().authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry.requestMatchers("/**").access(strapiAuthorizationManager))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    // 已登录但是无权限
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                        SecurityConfiguration.this.setAjaxResponse(response);
                        ViewResult<LoginView> viewViewResult;
                        log.info("[没有权限]", accessDeniedException);
                        viewViewResult = ViewResult.failure("[没有权限] " + accessDeniedException.getMessage(), null);
                        PrintWriter printWriter = response.getWriter();
                        printWriter.write(objectMapper.writeValueAsString(viewViewResult));
                        printWriter.flush();
                        printWriter.close();
                    });
                })
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    // 未登录没权限
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
                        SecurityConfiguration.this.setAjaxResponse(response);
                        ViewResult<LoginView> viewViewResult;
                        log.warn("[未登录] {}", authException.getMessage());
                        viewViewResult = ViewResult.failure("[未登录] " + authException.getMessage(), null);
                        PrintWriter printWriter = response.getWriter();
                        printWriter.write(objectMapper.writeValueAsString(viewViewResult));
                        printWriter.flush();
                        printWriter.close();
                    });
                })
                // 替换UsernamePasswordAuthenticationFilter
                .addFilterBefore(strapiAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
        strapiAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        return securityFilterChain;
    }

    @NotNull
    private StrapiAuthenticationFilter getStrapiAuthenticationFilter(ObjectMapper objectMapper) {
        StrapiAuthenticationFilter strapiAuthenticationFilter = new StrapiAuthenticationFilter(verifyCodeRequire, objectMapper);
        strapiAuthenticationFilter.setFilterProcessesUrl("/login/doLogin");
        strapiAuthenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            setAjaxResponse(response);
            PrintWriter printWriter = response.getWriter();
            List<String> authorities = ((UserBO) authentication.getPrincipal()).getAuthorityBOList().stream().map(AuthorityBO::getAuthority).map(Authority::getAuth).toList();
            List<Group> groups = ((UserBO) authentication.getPrincipal()).getGroupBOList().stream().map(GroupBO::getGroup).toList();
            String token = UUID.randomUUID(true).toString(true);
            ((UserBO) authentication.getPrincipal()).setToken(token);
            LoginView loginView = LoginView.builder().token(token).username(authentication.getName()).authorities(authorities).groupList(groups).build();
            ViewResult<LoginView> viewViewResult = ViewResult.success(loginView);
            printWriter.write(objectMapper.writeValueAsString(viewViewResult));
            printWriter.flush();
            printWriter.close();

        });
        strapiAuthenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            setAjaxResponse(response);
            ViewResult<LoginView> viewViewResult;
            if (exception instanceof BadVerifyCodeAuthenticationException) {
                viewViewResult = ViewResult.failure("验证码错误", null);
            } else {
                log.debug("[登录失败]", exception);
                viewViewResult = ViewResult.failure("[登录失败] " + exception.getMessage(), null);
            }
            PrintWriter printWriter = response.getWriter();
            printWriter.write(objectMapper.writeValueAsString(viewViewResult));
            printWriter.flush();
            printWriter.close();
        });
        // 使用session方式存储context
        strapiAuthenticationFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        return strapiAuthenticationFilter;
    }

    private void setAjaxResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=utf-8");
    }

}
