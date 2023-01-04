package com.mystrapi.strapi.persistance.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author tangqiang
 */
@Data
@Entity
@Table(name = "strapi_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_group_pk")
    @GenericGenerator(name = "strapi_group_pk", strategy = "identity")
    private Long id;

    @Basic(optional = false)
    @Column(name = "user_group", unique = true, nullable = false, length = 255)
    private String group;

}
