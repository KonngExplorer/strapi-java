package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "strapi_user_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_group_pk")
    @GenericGenerator(name = "strapi_user_group_pk", strategy = "identity")
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}