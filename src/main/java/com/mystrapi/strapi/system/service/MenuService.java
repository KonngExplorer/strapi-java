package com.mystrapi.strapi.system.service;

import cn.hutool.core.util.StrUtil;
import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import com.mystrapi.strapi.persistance.repository.strapi.MenuRepository;
import com.mystrapi.strapi.system.bo.MenuBo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * @author tangqiang
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public Optional<MenuBo> findMenuBoListByPath(String path) {

        if (StrUtil.isBlank(path)) {
            return Optional.empty();
        }

        MenuBo menuBo = new MenuBo();
        Optional<Menu> optionalMenu = menuRepository.findMenuByPath(path);

        optionalMenu.ifPresent(menu -> {
            menuBo.setMenu(menu);
            if (menu.getAuthority() != null) {
                menuBo.setAuthorities(Collections.singletonList(menu.getAuthority()));
            }
        });

        return Optional.of(menuBo);

    }

}
