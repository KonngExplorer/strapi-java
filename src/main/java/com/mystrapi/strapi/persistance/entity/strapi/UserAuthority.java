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
@Table(name = "strapi_user_authority")
public class UserAuthority extends BaseEntity{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_authority_pk")
    @GenericGenerator(name = "strapi_user_authority_pk", strategy = "identity")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "authority_id", nullable = false)
    private Long authorityId;

}
