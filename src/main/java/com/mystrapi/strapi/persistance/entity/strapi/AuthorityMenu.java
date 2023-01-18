package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strapi_authority_menu")
public class AuthorityMenu extends BaseEntity{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_authority_menu_pk")
    @GenericGenerator(name = "strapi_authority_menu_pk", strategy = "identity")
    private Long id;

    @Column(name = "authority_id")
    private Long authorityId;

    @Column(name = "menu_id")
    private Long menuId;
}
