package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup,Long> {

    /**
     * 根据用户id获取用户组信息列表
     * @param userId 用户id
     * @return List<UserGroup>
     */
    List<UserGroup> findUserGroupByUserId(Long userId);

    /**
     * 根据用户id删除用户组关系
     * @param userId 用户id
     * @return 是否删除成功
     */
    long deleteUserGroupByUserId(Long userId);

}
