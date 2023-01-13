package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {
    /**
     * 根据组id列表获取组列表信息
     * @param ids 组id列表
     * @return 组列表
     */
    List<Group> findGroupsByIdIn(List<Long> ids);
}
