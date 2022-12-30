package com.mystrapi.strapi.repository;

import com.mystrapi.strapi.jpa.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 * <p>
 * 用户存储库
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 获取全部用户
     *
     * @return List<User>
     */
    @Override
    @NotNull
    List<User> findAll();

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);

    /**
     * 根据用户名查找用户数
     *
     * @param username 用户名
     * @param enabled  是否启用
     * @return 用户数
     */
    long countUserByUsernameIsAndEnabled(String username, boolean enabled);


}
