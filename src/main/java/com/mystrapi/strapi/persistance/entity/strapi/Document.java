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
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_document_pk")
    @GenericGenerator(name = "strapi_document_pk", strategy = "identity")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    @JdbcType(LongVarcharJdbcType.class)
    private String content;

    @Column(name = "html_content")
    @JdbcType(LongVarcharJdbcType.class)
    private String htmlContent;

    @Column(name = "json_content")
    @JdbcType(LongVarcharJdbcType.class)
    private String jsonContent;

}
