package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Site;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tangqiang
 */
public interface SiteRepository extends JpaRepository<Site, Long> {

}
