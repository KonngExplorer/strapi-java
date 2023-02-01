package com.mystrapi.strapi.context;

import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.Group;
import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import com.mystrapi.strapi.persistance.entity.strapi.User;
import com.mystrapi.strapi.persistance.repository.strapi.AuthorityRepository;
import com.mystrapi.strapi.persistance.repository.strapi.GroupRepository;
import com.mystrapi.strapi.persistance.repository.strapi.MenuRepository;
import com.mystrapi.strapi.system.bo.AuthorityBO;
import com.mystrapi.strapi.system.bo.GroupBO;
import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tangqiang
 */
@RequiredArgsConstructor
public class StrapiApplicationContextAware implements ApplicationContextAware {

    private final UserService userService;
    private final AuthorityRepository authorityRepository;
    private final GroupRepository groupRepository;
    private final MenuRepository menuRepository;

    @Value("${strapi.init-data:false}")
    private Boolean initData;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (!initData) {
            return;
        }
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = User.builder().id(1L).username("admin").password("123456").enabled(true).build();
        Group group = Group.builder().id(1L).group("测试部门1").users(Collections.singletonList(user)).build();
        Authority authority = Authority.builder().id(1L).auth("ROLE_ADMIN").group(group).user(Collections.singletonList(user)).build();
        Menu menu = Menu.builder().name("用户信息").path("/user/userInfo").authority(authority).build();
        authorityRepository.save(authority);
        groupRepository.save(group);
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
        userService.createUser(userBO);
    }
}
