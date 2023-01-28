package com.mystrapi.strapi.security;

import cn.hutool.core.collection.CollUtil;
import com.mystrapi.strapi.bs.bo.MenuBo;
import com.mystrapi.strapi.bs.bo.UserBO;
import com.mystrapi.strapi.bs.service.MenuService;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author tangqiang
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StrapiAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final MenuService menuService;

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        AuthorizationManager.super.verify(authentication, requestAuthorizationContext);
    }

    /**
     * 后续应从缓存中获取按钮
     *
     * @param authentication              the {@link Supplier} of the {@link Authentication} to check
     * @param requestAuthorizationContext the object to check
     * @return AuthorizationDecision
     */
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {

        if (authentication.get() == null || authentication.get().getPrincipal() == null || !(authentication.get().getPrincipal() instanceof UserBO userBO)) {
            return new AuthorizationDecision(false);
        }

        String requestUri = requestAuthorizationContext.getRequest().getRequestURI();
        log.info("requestUri --> {}", requestUri);

        Optional<MenuBo> optionalMenuBo = menuService.findMenuBoListByPath(requestUri);

        AtomicReference<AuthorizationDecision> authorizationDecision = new AtomicReference<>();

        optionalMenuBo.ifPresentOrElse(menuBo -> {
            Collection<? extends GrantedAuthority> grantedAuthorities = userBO.getAuthorities();
            if (grantedAuthorities != null && grantedAuthorities.size() > 0) {
                boolean contains = CollUtil.containsAny(
                        menuBo.getAuthorities() != null ? menuBo.getAuthorities().stream().map(Authority::getAuth).toList() : new ArrayList<>(),
                        userBO.getAuthorities() != null ? userBO.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList() : new ArrayList<>());
                authorizationDecision.set(new AuthorizationDecision(contains));
            } else {
                authorizationDecision.set(new AuthorizationDecision(false));
            }

        }, () -> authorizationDecision.set(new AuthorizationDecision(false)));

        return authorizationDecision.get();
    }
}
