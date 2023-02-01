package com.mystrapi.strapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.Group;
import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import com.mystrapi.strapi.persistance.entity.strapi.User;
import com.mystrapi.strapi.persistance.repository.strapi.AuthorityRepository;
import com.mystrapi.strapi.persistance.repository.strapi.GroupRepository;
import com.mystrapi.strapi.persistance.repository.strapi.MenuRepository;
import com.mystrapi.strapi.persistance.repository.strapi.UserRepository;
import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.WHEN_AVAILABLE,
args = "--spring.profiles.active=unit-test")
@Slf4j
class StrapiJavaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testPropertySources() {
        MockEnvironment environment = new MockEnvironment();
        TestPropertyValues.of("org=Spring", "name=Boot").applyTo(environment);
        log.info(environment.getProperty("name"));
    }

    /**
     * 在所有测试方法之前执行（仅执行一次）
     */
    @Test
    @Order(0)
    @SuppressWarnings({"unused"})
    void testSaveUser() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Group group = Group.builder().id(1L).group("测试部门1").users(Collections.singletonList(user)).build();
        Authority authority = Authority.builder().id(1L).auth("ROLE_ADMIN").group(group).user(Collections.singletonList(user)).build();
        Menu menu = Menu.builder().name("用户信息").path("/user/userInfo").authority(authority).build();
        applicationContext.getBean(UserRepository.class).save(user);
        applicationContext.getBean(GroupRepository.class).save(group);
        applicationContext.getBean(AuthorityRepository.class).save(authority);
        applicationContext.getBean(MenuRepository.class).save(menu);

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

        UserBO userBO1 = (UserBO) applicationContext.getBean(UserService.class).loadUserByUsername("admin");
        try {
            log.info("user {}", applicationContext.getBean(ObjectMapper.class).writeValueAsString(userBO1.getUser()));
            Assertions.assertNotNull(userBO.getUser(), "user 不为 null");
        } catch (JsonProcessingException e) {
            Assertions.fail("should not be called", e);
        }
    }
}
