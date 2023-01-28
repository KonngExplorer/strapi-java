package com.mystrapi.strapi.web.controller;

import com.mystrapi.strapi.system.bo.UserBO;
import com.mystrapi.strapi.system.service.UserService;
import com.mystrapi.strapi.web.view.ViewResult;
import com.mystrapi.strapi.web.view.user.UserInfoView;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangqiang
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/userInfo")
    public ViewResult<UserInfoView> userInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            UserBO userBO = ((UserBO) principal);
            UserInfoView userInfoView = UserInfoView.builder()
                    .id(userBO.getUser().getId())
                    .username(userBO.getUsername())
                    .enabled(userBO.isEnabled())
                    .authorities(userBO.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .build();
            return ViewResult.success(userInfoView);
        }
        return ViewResult.failure("用户未登录或者获取用户信息失败", null);
    }

}
