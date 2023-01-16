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
@Table(name = "strapi_group")
public class Group extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_group_pk")
    @GenericGenerator(name = "strapi_group_pk", strategy = "identity")
    private Long id;

    @Column(name = "user_group", unique = true, nullable = false, length = 255)
    private String group;

}
