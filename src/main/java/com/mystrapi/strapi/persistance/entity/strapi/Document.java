package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strapi_document")
public class Document extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_pk")
    @GenericGenerator(name = "strapi_user_pk", strategy = "identity")
    private Long id;

    @Basic(optional = false)
    @Column(name = "title")
    private String title;

    @Basic(optional = true)
    @Column(name = "content")
    @JdbcType(LongVarcharJdbcType.class)
    private String content;

}
