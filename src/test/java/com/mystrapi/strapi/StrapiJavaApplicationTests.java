package com.mystrapi.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.*;
import com.mystrapi.strapi.persistance.repository.strapi.*;
import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.WHEN_AVAILABLE)
@Slf4j
class StrapiJavaApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testPropertySources() {
        MockEnvironment environment = new MockEnvironment();
        TestPropertyValues.of("org=Spring", "name=Boot").applyTo(environment);
        log.info(environment.getProperty("name"));
    }

    @Test
    void initSomeUser() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Group group = Group.builder().id(1L).group("测试部门1").users(Collections.singletonList(user)).build();
        Authority authority = Authority.builder().id(1L).auth("ROLE_ADMIN").group(group).user(user).build();
        Menu menu = Menu.builder().name("用户信息").path("/user/userInfo").authority(authority).build();
        userRepository.save(user);
        groupRepository.save(group);
        authorityRepository.save(authority);
        menuRepository.save(menu);

        AuthorityBO userAuthorityBO = AuthorityBO.builder().authority(authority).build();
        AuthorityBO groupAuthorityBO = AuthorityBO.builder().authority(authority).build();
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
    }

}
