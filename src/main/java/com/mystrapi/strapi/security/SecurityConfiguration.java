package com.mystrapi.strapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.util.StrapiUtil;
import com.mystrapi.strapi.web.view.ViewResult;
import com.mystrapi.strapi.web.view.login.LoginView;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionManagementFilter;

import java.io.PrintWriter;

/**
 * 安全配置
 *
 * @author tangqiang
 */
@Configuration
@EnableWebSecurity(debug = true)
@Slf4j
public class SecurityConfiguration {

    @Value("${strapi.login.verifyCodeRequire:false}")
    private Boolean verifyCodeRequire;

    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http, ObjectMapper objectMapper, StrapiAuthorizationManager strapiAuthorizationManager) throws Exception {
        SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
        StrapiAuthenticationFilter strapiAuthenticationFilter = getStrapiAuthenticationFilter(objectMapper, securityContextRepository);
        SecurityFilterChain securityFilterChain = http
                .csrf().disable()
                .cors().disable()
                .anonymous().disable()
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                )
                // 替换UsernamePasswordAuthenticationFilter
                .addFilterBefore(strapiAuthenticationFilter, SessionManagementFilter.class)
                .sessionManagement(configurer -> {
                    configurer.enableSessionUrlRewriting(true)
                            .sessionConcurrency(concurrencyControlConfigurer -> {
                                concurrencyControlConfigurer
                                        // 单个账号最大支持多少设备同时在线
                                        .maximumSessions(1)
                                        // true：其他设备中已登录的话，本设备登录不了；false：其他设备已登录，本设备登录会挤掉其他设备登录用户状态
                                        .maxSessionsPreventsLogin(false)
                                        .expiredSessionStrategy(event -> {
                                            StrapiUtil.setAjaxResponse(event.getResponse());
                                            ViewResult<LoginView> viewViewResult;
                                            viewViewResult = ViewResult.failure("[已在其他设备中登录！请重新登录]", null);
                                            PrintWriter printWriter = event.getResponse().getWriter();
                                            printWriter.write(objectMapper.writeValueAsString(viewViewResult));
                                            printWriter.flush();
                                            printWriter.close();
                                        });
                            })
                            .sessionAuthenticationFailureHandler((request, response, exception) -> {
                                StrapiUtil.setAjaxResponse(response);
                                ViewResult<LoginView> viewViewResult;
                                log.warn("[登录失败] ", exception);

                                String message = exception.getMessage();
                                if (exception instanceof SessionAuthenticationException) {
                                    message = "已在其他设备中登录";
                                }

                                viewViewResult = ViewResult.failure("[登录失败] " + message, null);
                                PrintWriter printWriter = response.getWriter();
                                printWriter.write(objectMapper.writeValueAsString(viewViewResult));
                                printWriter.flush();
                                printWriter.close();
                            });
                })
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/login/doLogin").permitAll()
                                .requestMatchers("/code/img").permitAll()
                                .requestMatchers("/**").access(strapiAuthorizationManager))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    // 已登录但是无权限
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                        StrapiUtil.setAjaxResponse(response);
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
                    // 未登录导致没权限
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint((request, response, authException) -> {
                        StrapiUtil.setAjaxResponse(response);
                        ViewResult<LoginView> viewViewResult;
                        log.warn("[未登录] ", authException);
                        viewViewResult = ViewResult.failure("[未登录] " + authException.getMessage(), null);
                        PrintWriter printWriter = response.getWriter();
                        printWriter.write(objectMapper.writeValueAsString(viewViewResult));
                        printWriter.flush();
                        printWriter.close();
                    });
                })
                .build();
        strapiAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        return securityFilterChain;
    }

    @NotNull
    private StrapiAuthenticationFilter getStrapiAuthenticationFilter(ObjectMapper objectMapper, SecurityContextRepository securityContextRepository) {
        StrapiAuthenticationFilter strapiAuthenticationFilter = new StrapiAuthenticationFilter(verifyCodeRequire, objectMapper);
        strapiAuthenticationFilter.setFilterProcessesUrl("/login/doLogin");
        // ForwardAuthenticationSuccessHandler
        strapiAuthenticationFilter.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/user/userInfo"));
        strapiAuthenticationFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            StrapiUtil.setAjaxResponse(response);
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
        strapiAuthenticationFilter.setSecurityContextRepository(securityContextRepository);
        strapiAuthenticationFilter.setContinueChainBeforeSuccessfulAuthentication(false);
        return strapiAuthenticationFilter;
    }

    private StrapiAuthenticationResponseFilter getStrapiAuthenticationResponseFilter(ObjectMapper objectMapper, SecurityContextRepository securityContextRepository) {
        return new StrapiAuthenticationResponseFilter(objectMapper, securityContextRepository);
    }

}
