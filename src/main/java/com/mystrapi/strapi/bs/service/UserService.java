package com.mystrapi.strapi.bs.service;

import com.mystrapi.strapi.bs.bo.AuthorityBO;
import com.mystrapi.strapi.bs.bo.GroupBO;
import com.mystrapi.strapi.bs.bo.UserBO;
import com.mystrapi.strapi.persistance.entity.strapi.*;
import com.mystrapi.strapi.persistance.repository.strapi.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final UserGroupRepository userGroupRepository;
    private final GroupAuthorityRepository groupAuthorityRepository;

    public UserService(UserRepository userRepository,
                       AuthorityRepository authorityRepository,
                       UserAuthorityRepository userAuthorityRepository,
                       GroupRepository groupRepository,
                       UserGroupRepository userGroupRepository,
                       GroupAuthorityRepository groupAuthorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.groupAuthorityRepository = groupAuthorityRepository;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createUser(UserDetails userDetails) {
        UserBO userBO = (UserBO) userDetails;
        userRepository.save(userBO.getUser());

        List<Authority> uAuthorities = userBO.getAuthorityBOList().stream().map(AuthorityBO::getAuthority).toList();
        List<Authority> gAuthorities = userBO.getGroupBOList().stream()
                .flatMap(groupBO -> groupBO.getAuthorityBOList().stream())
                .map(AuthorityBO::getAuthority)
                .toList();
        List<Authority> authorities = Stream.of(uAuthorities, gAuthorities)
                .flatMap(Collection::stream).unordered().distinct().collect(Collectors.toList());
        authorityRepository.saveAll(authorities);

        List<UserAuthority> userAuthorities = authorities.stream().map(authority -> {
            long authorityId = authority.getId();
            return UserAuthority.builder().userId(userBO.getUser().getId()).authorityId(authorityId).build();
        }).toList();
        userAuthorityRepository.saveAll(userAuthorities);

        List<Group> groups = userBO.getGroupBOList().stream().map(GroupBO::getGroup).toList();
        groupRepository.saveAll(groups);

        List<UserGroup> userGroups = groups.stream()
                .map(group -> UserGroup.builder().groupId(group.getId()).userId(userBO.getUser().getId()).build())
                .toList();
        userGroupRepository.saveAll(userGroups);

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

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateUser(UserDetails userDetails) {
        UserBO userBO = (UserBO) userDetails;
        userRepository.save(((UserBO) userDetails).getUser());
        userAuthorityRepository.deleteUserAuthoritiesByUserId(userBO.getUser().getId());
        List<UserAuthority> userAuthorities = userBO.getAuthorityBOList().stream()
                .map(authorityBO -> UserAuthority.builder()
                        .authorityId(authorityBO.getAuthority().getId())
                        .userId(userBO.getUser().getId()).build())
                .toList();
        userAuthorityRepository.saveAll(userAuthorities);
        userGroupRepository.deleteUserGroupByUserId(userBO.getUser().getId());
        List<UserGroup> userGroups = userBO.getGroupBOList().stream().map(groupBO -> UserGroup.builder()
                .userId(userBO.getUser().getId())
                .groupId(groupBO.getGroup().getId()).build()).toList();
        userGroupRepository.saveAll(userGroups);
        // TODO 从缓存中去除
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteUser(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        long userId = ((UserBO) userDetails).getUser().getId();
        userAuthorityRepository.deleteUserAuthoritiesByUserId(userId);
        userGroupRepository.deleteUserGroupByUserId(userId);
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

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        List<UserAuthority> userAuthorities = userAuthorityRepository.findUserAuthoritiesByUserId(user.getId());
        List<Authority> uAuthorities = authorityRepository.findAuthoritiesByIdIn(userAuthorities.stream().map(UserAuthority::getAuthorityId).toList());
        List<UserGroup> userGroups = userGroupRepository.findUserGroupByUserId(user.getId());
        List<Group> groups = groupRepository.findGroupsByIdIn(userGroups.stream().map(UserGroup::getGroupId).toList());
        List<GroupAuthority> groupAuthorities = groupAuthorityRepository.findGroupAuthoritiesByGroupIdIn(groups.stream().map(Group::getId).toList());
        List<Authority> gAuthorities = authorityRepository.findAuthoritiesByIdIn(groupAuthorities.stream().map(GroupAuthority::getAuthorityId).toList());

        UserBO userBO = UserBO.builder()
                .user(user)
                .groupBOList(groups.stream().map(group -> GroupBO.builder()
                        .group(group)
                        .authorityBOList(gAuthorities.stream().map(AuthorityBO::new).toList())
                        .build()).toList())
                .authorityBOList(uAuthorities.stream().map(AuthorityBO::new).toList())
                .build();
        userBO.getGroupBOList().forEach(groupBO -> groupBO.setUserBOList(Collections.singletonList(userBO)));
        return userBO;
    }

    @PostConstruct
    public void initSomeUsers() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Authority authority4u = Authority.builder().id(1L).auth("ROLE_ADMIN").build();
        Authority authority4g = Authority.builder().id(1L).auth("ROLE_ADMIN").build();
        AuthorityBO userAuthorityBO = AuthorityBO.builder().authority(authority4u).build();
        AuthorityBO groupAuthorityBO = AuthorityBO.builder().authority(authority4g).build();
        Group group = Group.builder().id(1L).group("测试部门1").build();
        GroupBO groupBO = GroupBO.builder()
                .group(group)
                .userBOList(new ArrayList<>())
                .authorityBOList(Collections.singletonList(groupAuthorityBO)).build();
        UserBO userBO = UserBO.builder()
                .user(user)
                .authorityBOList(List.of(userAuthorityBO))
                .groupBOList(Collections.singletonList(groupBO))
                .passwordEncoder(passwordEncoder::encode).build();
        groupBO.setUserBOList(Collections.singletonList(userBO));
        this.createUser(userBO);
    }


}
