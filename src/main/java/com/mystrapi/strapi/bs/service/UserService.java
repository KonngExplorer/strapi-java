package com.mystrapi.strapi.bs.service;

import com.mystrapi.strapi.bs.bo.AuthorityBO;
import com.mystrapi.strapi.bs.bo.GroupBO;
import com.mystrapi.strapi.bs.bo.UserBO;
import com.mystrapi.strapi.persistance.entity.strapi.*;
import com.mystrapi.strapi.persistance.repository.strapi.*;
import com.mystrapi.strapi.web.view.ViewResult;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Service
@Slf4j
public class UserService implements UserDetailsManager {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupAuthorityRepository groupAuthorityRepository;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository, UserAuthorityRepository userAuthorityRepository, GroupRepository groupRepository, GroupUserRepository groupUserRepository, GroupAuthorityRepository groupAuthorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.groupAuthorityRepository = groupAuthorityRepository;
    }

    public ViewResult<List<User>> findAllUser() {
        return ViewResult.success(userRepository.findAll());
    }

    @Override
    public void createUser(UserDetails userDetails) {
        UserBO userBO = (UserBO) userDetails;
        User user = userRepository.save(userBO.getUser());
//        userBO.setUser(user);

        List<Authority> authorities = userBO.getAuthorityBOList().stream().map(AuthorityBO::getAuthority).toList();
        authorities = authorityRepository.saveAll(authorities);
//        userBO.setAuthorityBOList(authorities.stream().map(AuthorityBO::new).toList());

        List<UserAuthority> userAuthorities = authorities.stream().map(authority -> {
            long authorityId = authority.getId();
            return UserAuthority.builder().userId(user.getId()).authorityId(authorityId).build();
        }).toList();
        userAuthorityRepository.saveAll(userAuthorities);

        List<Group> groups = userBO.getGroupBOList().stream().map(GroupBO::getGroup).toList();
        groupRepository.saveAll(groups);

        List<GroupUser> groupUsers = groups.stream()
                .map(group -> GroupUser.builder().groupId(group.getId()).userId(user.getId()).build())
                .toList();
        groupUserRepository.saveAll(groupUsers);

        List<GroupAuthority> allGroupAuthorities = new ArrayList<>();
        for (Group group : groups) {
            List<GroupAuthority> groupAuthorities = authorities.stream()
                    .map(authority -> GroupAuthority.builder()
                            .authorityId(authority.getId())
                            .groupId(group.getId())
                            .build())
                    .toList();
            allGroupAuthorities.addAll(groupAuthorities);
        }
        groupAuthorityRepository.saveAll(allGroupAuthorities);
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        UserBO userBO = (UserBO) userDetails;
        User user = userRepository.save(((UserBO) userDetails).getUser());
        userAuthorityRepository.deleteUserAuthoritiesByUserId(user.getId());
        List<UserAuthority> userAuthorities = userBO.getAuthorityBOList().stream()
                .map(authorityBO -> UserAuthority.builder()
                        .authorityId(authorityBO.getAuthority().getId())
                        .userId(userBO.getUser().getId()).build())
                .toList();
        userAuthorityRepository.saveAll(userAuthorities);
        // TODO 从缓存中去除
    }

    @Override
    public void deleteUser(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        long userId = ((UserBO) userDetails).getUser().getId();
        userAuthorityRepository.deleteUserAuthoritiesByUserId(userId);
        userRepository.deleteById(userId);
        // TODO 从缓存中去除
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        if (this.authenticationManager != null) {
            log.debug("Reauthenticating user '{}' for password change request.", username);
            this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            log.debug("No authentication manager set. Password won't be re-checked.");
        }
        log.debug("Changing password for user '" + username + "'");
        User user = userRepository.findByUsername(username);
        user.setPassword(newPassword);
        User newUser = userRepository.save(user);
        UserBO userDetails = UserBO.builder().user(newUser).build();
        Authentication newAuthentication = createNewAuthentication(currentUser, userDetails);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(newAuthentication);
        this.securityContextHolderStrategy.setContext(context);
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        List<UserAuthority> userAuthorities = userAuthorityRepository.findUserAuthoritiesByUserId(user.getId());
        List<Authority> authorities = authorityRepository.findAuthoritiesByIdIn(userAuthorities.stream().map(UserAuthority::getAuthorityId).toList());
        return UserBO.builder()
                .user(user)
                .authorityBOList(authorities.stream().map(AuthorityBO::new).toList())
                .build();
    }

    @PostConstruct
    public void initSomeUsers() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Authority authority = Authority.builder().id(1L).auth("ROLE_ADMIN").build();
        AuthorityBO authorityBO = AuthorityBO.builder().authority(authority).build();
        Group group = Group.builder().id(1L).group("测试部门1").build();
        GroupBO groupBO = GroupBO.builder()
                .group(group)
                .userBOList(new ArrayList<>())
                .authorityBOList(Collections.singletonList(authorityBO)).build();
        UserBO userBO = UserBO.builder()
                .user(user)
                .authorityBOList(List.of(authorityBO))
                .groupBOList(Collections.singletonList(groupBO))
                .passwordEncoder(passwordEncoder::encode).build();
        groupBO.setUserBOList(Collections.singletonList(userBO));
        this.createUser(userBO);
    }

}
