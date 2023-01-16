package com.mystrapi.strapi.web.view.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Builder
public class UserInfoView {

    private Long id;

    private String username;

    private Boolean enabled;

    private List<String> authorities;

}
