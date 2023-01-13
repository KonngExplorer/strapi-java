package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    /**
     * 删除关联关系
     * @param userId 用户id
     * @return 删除数
     */
    long deleteUserAuthoritiesByUserId(long userId);

    /**
     * 根据用户id获取用户权限列表
     * @param userId 用户id
     * @return 用户权限列表
     */
    List<UserAuthority> findUserAuthoritiesByUserId(long userId);
}
