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
@Table(name = "strapi_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

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
