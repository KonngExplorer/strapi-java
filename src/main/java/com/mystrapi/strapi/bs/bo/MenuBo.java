package com.mystrapi.strapi.bs.bo;


import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuBo {
    private Menu menu;
    private List<Authority> authorities;
}
