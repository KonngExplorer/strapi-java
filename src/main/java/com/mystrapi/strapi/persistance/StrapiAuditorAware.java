package com.mystrapi.strapi.persistance;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author tangqiang
 */
@Component("strapiAuditorAware")
@Slf4j
public class StrapiAuditorAware implements AuditorAware<String> {
    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        String username =
                Optional.of(ctx).map(SecurityContext::getAuthentication)
                        .map(authentication -> authentication.getPrincipal().toString()).orElse("system");
        return Optional.of(username);
    }
}
