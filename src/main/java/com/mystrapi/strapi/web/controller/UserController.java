package com.mystrapi.strapi.web.controller;

import com.mystrapi.strapi.jpa.entity.User;
import com.mystrapi.strapi.service.UserService;
import com.mystrapi.strapi.view.ViewResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tangqiang
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @GetMapping("/findAllUser")
    public ViewResult<List<User>> findAllUser() {
        return userService.findAllUser();
    }

}
