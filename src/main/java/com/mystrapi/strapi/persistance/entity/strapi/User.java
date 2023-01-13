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
@Table(name = "strapi_user")
public class User extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_pk")
    @GenericGenerator(name = "strapi_user_pk", strategy = "identity")
    private Long id;

    @Basic(optional = false)
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Basic(optional = false)
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Basic(optional = false)
    @Column(name = "enabled", nullable = false, length = 1)
    private Boolean enabled;

}
