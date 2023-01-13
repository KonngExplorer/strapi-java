package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.GroupAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Administrator
 */
public interface GroupAuthorityRepository extends JpaRepository<GroupAuthority,Long> {
}
