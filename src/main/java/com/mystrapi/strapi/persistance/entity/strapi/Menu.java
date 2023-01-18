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
@Table(name = "strapi_menu")
public class Menu extends BaseEntity{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_menu_pk")
    @GenericGenerator(name = "strapi_menu_pk", strategy = "identity")
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;
}
