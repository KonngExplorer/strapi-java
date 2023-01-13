package com.mystrapi.strapi.web.controller;

import com.mystrapi.strapi.persistance.entity.strapi.User;
import com.mystrapi.strapi.bs.service.UserService;
import com.mystrapi.strapi.web.view.ViewResult;
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

}
