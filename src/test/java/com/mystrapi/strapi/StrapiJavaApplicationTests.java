package com.mystrapi.strapi;

import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.system.service.UserService;
import com.mystrapi.strapi.persistance.entity.strapi.*;
import com.mystrapi.strapi.persistance.repository.strapi.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@RequiredArgsConstructor
class StrapiJavaApplicationTests {

    private final UserService userService;
    private final AuthorityRepository authorityRepository;
    private final GroupRepository groupRepository;
    private final MenuRepository menuRepository;
    private final GroupAuthorityRepository groupAuthorityRepository;
    private final AuthorityMenuRepository authorityMenuRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void initSomeUser() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Authority authority = Authority.builder().id(1L).auth("ROLE_ADMIN").build();
        AuthorityBO userAuthorityBO = AuthorityBO.builder().authority(authority).build();
        AuthorityBO groupAuthorityBO = AuthorityBO.builder().authority(authority).build();
        authorityRepository.save(authority);

        Group group = Group.builder().id(1L).group("测试部门1").build();
        GroupBO groupBO = GroupBO.builder()
                .group(group)
                .userBOList(new ArrayList<>())
                .authorityBOList(Collections.singletonList(groupAuthorityBO)).build();
        groupRepository.save(group);

        List<GroupAuthority> groupAuthorities = Stream.of(authority)
                .map(authority1 -> GroupAuthority.builder()
                        .authorityId(authority1.getId())
                        .groupId(group.getId())
                        .build())
                .toList();
        List<GroupAuthority> allGroupAuthorities = new ArrayList<>(groupAuthorities);
        groupAuthorityRepository.saveAll(allGroupAuthorities);

        Menu menu = Menu.builder().name("用户信息").path("/user/userInfo").build();
        menuRepository.save(menu);
        AuthorityMenu authorityMenu = AuthorityMenu.builder().authorityId(authority.getId()).menuId(menu.getId()).build();
        authorityMenuRepository.save(authorityMenu);

        UserBO userBO = UserBO.builder()
                .user(user)
                .authorityBOList(List.of(userAuthorityBO))
                .groupBOList(Collections.singletonList(groupBO))
                .passwordEncoder(passwordEncoder::encode).build();
        groupBO.setUserBOList(Collections.singletonList(userBO));
        userService.createUser(userBO);
    }

}
