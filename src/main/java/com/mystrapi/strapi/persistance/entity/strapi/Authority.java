package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strapi_authority")
@ToString(exclude = {"menus", "user", "group"})
public class Authority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_authority_pk")
    @GenericGenerator(name = "strapi_authority_pk", strategy = "identity")
    private Long id;

    @Column(name = "auth", unique = true, nullable = false, length = 100)
    private String auth;

    @OneToMany
    @JoinTable(name = "strapi_authority_menu", joinColumns = @JoinColumn(name = "authority_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private List<Menu> menus;

    @ManyToOne
    @JoinTable(name = "strapi_user_authority", joinColumns = @JoinColumn(name = "authority_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user;

    @ManyToOne
    @JoinTable(name = "strapi_group_authority", joinColumns = @JoinColumn(name = "authority_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Group group;

}
