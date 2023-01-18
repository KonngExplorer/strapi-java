package com.mystrapi.strapi.bs.service;

import cn.hutool.core.util.StrUtil;
import com.mystrapi.strapi.bs.bo.MenuBo;
import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import com.mystrapi.strapi.persistance.entity.strapi.AuthorityMenu;
import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import com.mystrapi.strapi.persistance.repository.strapi.AuthorityMenuRepository;
import com.mystrapi.strapi.persistance.repository.strapi.AuthorityRepository;
import com.mystrapi.strapi.persistance.repository.strapi.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author tangqiang
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final AuthorityMenuRepository authorityMenuRepository;
    private final AuthorityRepository authorityRepository;

    public Optional<MenuBo> findMenuBoListByPath(String path) {

        if (StrUtil.isBlank(path)) {
            return Optional.empty();
        }

        MenuBo menuBo = new MenuBo();
        Optional<Menu> optionalMenu = menuRepository.findMenuByPath(path);

        optionalMenu.ifPresent(menu -> {
            menuBo.setMenu(menu);
            List<AuthorityMenu> authorityMenus = authorityMenuRepository.findAuthorityMenuByMenuId(menu.getId());
            List<Authority> authorities = authorityRepository.findAllById(authorityMenus.stream().map(AuthorityMenu::getAuthorityId).toList());
            menuBo.setAuthorities(authorities);
        });

        return Optional.of(menuBo);

    }

}
