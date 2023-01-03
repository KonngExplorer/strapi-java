package com.mystrapi.strapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.bo.AuthorityBO;
import com.mystrapi.strapi.bo.UserBO;
import com.mystrapi.strapi.jpa.entity.Authority;
import com.mystrapi.strapi.view.ViewResult;
import com.mystrapi.strapi.view.login.LoginView;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

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
    private final ObjectMapper objectMapper;

    public SecurityConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {
        StrapiAuthenticationFilter strapiAuthenticationFilter = new StrapiAuthenticationFilter(verifyCodeRequire, objectMapper);
        strapiAuthenticationFilter.setFilterProcessesUrl("/login/doLogin");
        strapiAuthenticationFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            setAjaxResponse(response);
            PrintWriter printWriter = response.getWriter();
            List<String> authorities = ((UserBO) authentication.getPrincipal()).getAuthorityBOList().stream().map(AuthorityBO::getAuthority).map(Authority::getAuth).toList();
            LoginView loginView = LoginView.builder().username(authentication.getName()).authorities(authorities).build();
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
        strapiAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        strapiAuthenticationFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        SecurityFilterChain securityFilterChain = http
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests()
                .requestMatchers("/**").authenticated()
                .and()
                .addFilterBefore(strapiAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
        strapiAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        return securityFilterChain;
    }

    private void setAjaxResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=utf-8");
    }

}
