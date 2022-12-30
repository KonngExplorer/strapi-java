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
@Data
@Entity
@Table(name = "strapi_authority")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_authority_pk")
    @GenericGenerator(name = "strapi_authority_pk", strategy = "identity")
    private Long id;

    @Basic(optional = false)
    @Column(name = "auth", unique = true, nullable = false, length = 100)
    private String auth;

}
