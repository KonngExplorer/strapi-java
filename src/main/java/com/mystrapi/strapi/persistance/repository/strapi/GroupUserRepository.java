package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tangqiang
 */
public interface GroupUserRepository extends JpaRepository<UserGroup,Long> {
}
