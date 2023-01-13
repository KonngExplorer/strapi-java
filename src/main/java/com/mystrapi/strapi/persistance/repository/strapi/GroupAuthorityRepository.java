package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.GroupAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Administrator
 */
@Repository
public interface GroupAuthorityRepository extends JpaRepository<GroupAuthority,Long> {

    /**
     * 根据组id列表获取组关系列表
     * @param groupIds 组id列表
     * @return 组权限关系列表
     */
    List<GroupAuthority> findGroupAuthoritiesByGroupIdIn(List<Long> groupIds);

}
