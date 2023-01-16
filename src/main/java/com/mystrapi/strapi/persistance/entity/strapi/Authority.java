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
@Table(name = "strapi_authority")
public class Authority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_authority_pk")
    @GenericGenerator(name = "strapi_authority_pk", strategy = "identity")
    private Long id;

    @Column(name = "auth", unique = true, nullable = false, length = 100)
    private String auth;

}
