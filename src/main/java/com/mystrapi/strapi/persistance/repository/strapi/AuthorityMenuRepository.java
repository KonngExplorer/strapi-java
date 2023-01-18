package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.AuthorityMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author tangqiang
 */
@Repository
public interface AuthorityMenuRepository extends JpaRepository<AuthorityMenu, Long> {

    /**
     * 根据权限id列表查询权限-按钮列表
     * @param authorityIds 权限id列表
     * @return 权限-按钮列表
     */
    List<AuthorityMenu> findAuthorityMenusByAuthorityIdIn(List<Long> authorityIds);

    /**
     * 根据按钮id查询权限-按钮列表
     * @param menuId 按钮id
     * @return 权限-按钮列表
     */
    List<AuthorityMenu> findAuthorityMenuByMenuId(Long menuId);

}
