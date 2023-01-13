package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * 根据id列表返回权限列表
     * @param ids id集合
     * @return 权限集合
     */
    List<Authority> findAuthoritiesByIdIn(List<Long> ids);

}
