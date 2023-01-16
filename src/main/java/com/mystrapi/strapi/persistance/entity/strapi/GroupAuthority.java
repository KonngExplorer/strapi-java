package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "strapi_group_authority")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupAuthority extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_group_authority_pk")
    @GenericGenerator(name = "strapi_group_authority_pk", strategy = "identity")
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "authority_id", nullable = false)
    private Long authorityId;
}
