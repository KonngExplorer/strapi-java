package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author tangqiang
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 根据path查询按钮列表
     * @param path 请求路径
     * @return 按钮列表
     */
    Optional<Menu> findMenuByPath(String path);

}
