package com.mystrapi.strapi.system.service;

import cn.hutool.core.util.ObjectUtil;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.User;
import com.mystrapi.strapi.persistance.repository.strapi.*;
import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private final AuthenticationConfiguration authenticationConfiguration;

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final GroupRepository groupRepository;
    private final MenuRepository menuRepository;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createUser(UserDetails userDetails) {
        UserBO userBO = (UserBO) userDetails;
        userRepository.save(userBO.getUser());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateUser(UserDetails userDetails) {
        userRepository.save(((UserBO) userDetails).getUser());
        // TODO 从缓存中去除
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteUser(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        long userId = ((UserBO) userDetails).getUser().getId();
        userRepository.deleteById(userId);
        // TODO 从缓存中去除
    }

    /**
     * 修改密码后需要重新鉴权
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            if (authenticationManager != null) {
                log.debug("ReAuthenticating user '{}' for password change request.", username);
                authenticationManager
                        .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
            } else {
                log.debug("No authentication manager set. Password won't be re-checked.");
            }
            log.debug("Changing password for user '" + username + "'");
            User user = userRepository.findByUsername(username);
            user.setPassword(newPassword);
            userRepository.save(user);
            UserBO userDetails = (UserBO) loadUserByUsername(user.getUsername());
            Authentication newAuthentication = createNewAuthentication(currentUser, userDetails);
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(newAuthentication);
            this.securityContextHolderStrategy.setContext(context);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    protected Authentication createNewAuthentication(@NotNull Authentication currentAuth, UserBO userDetails) {
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(userDetails,
                null, userDetails.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.countUserByUsernameIsAndEnabled(username, true) > 0;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (ObjectUtil.isNull(user)) {
            throw new UsernameNotFoundException("用户名 [{}] 不存在");
        }

        List<AuthorityBO> authorityBOList = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            authorityBOList.add(AuthorityBO.builder().authority(authority).menuList(authority.getMenus()).build());
        }
        UserBO userBO = UserBO.builder()
                .user(user)
                .groupBOList(
                        Collections.singletonList(GroupBO.builder().group(user.getGroup()).authorityBOList(authorityBOList).build())
                )
                .build();
        userBO.getGroupBOList().forEach(groupBO -> groupBO.setUserBOList(Collections.singletonList(userBO)));
        return userBO;
    }

}
