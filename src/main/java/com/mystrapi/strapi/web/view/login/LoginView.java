package com.mystrapi.strapi.web.view.login;

import com.mystrapi.strapi.persistance.entity.strapi.Group;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Builder
public class LoginView {
    private String token;
    private String username;
    private List<String> authorities;
    private List<Group> groupList;
}
