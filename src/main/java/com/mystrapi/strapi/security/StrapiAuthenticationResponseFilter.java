package com.mystrapi.strapi.security;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.Group;
import com.mystrapi.strapi.util.StrapiUtil;
import com.mystrapi.strapi.web.view.ViewResult;
import com.mystrapi.strapi.web.view.login.LoginView;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author tangqiang
 */
public class StrapiAuthenticationResponseFilter extends OncePerRequestFilter {

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private final SecurityContextRepository securityContextRepository;
    private final ObjectMapper objectMapper;


    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public StrapiAuthenticationResponseFilter(ObjectMapper objectMapper, SecurityContextRepository securityContextRepository) {
        this.objectMapper = objectMapper;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        Assert.notNull(securityContextRepository, "SecurityContextRepository cannot be null");
        if (this.securityContextRepository.containsContext(request)) {
            Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
            if (authentication != null && !this.trustResolver.isAnonymous(authentication)) {
                // 已登录请求（已鉴权）
                StrapiUtil.setAjaxResponse(response);
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
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
