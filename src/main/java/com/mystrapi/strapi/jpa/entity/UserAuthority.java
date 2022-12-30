package com.mystrapi.strapi.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author tangqiang
 */
@Entity
@Table(name = "strapi_user_authority")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuthority {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_authority_pk")
    @GenericGenerator(name = "strapi_user_authority_pk", strategy = "identity")
    private Long id;

    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Basic(optional = false)
    @Column(name = "authority_id", nullable = false)
    private Long authorityId;

}
