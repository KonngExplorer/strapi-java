package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * @author tangqiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strapi_site")
@ToString(exclude = {"channels"})
public class Site extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_site_pk")
    @GenericGenerator(name = "strapi_site_pk", strategy = "identity")
    private Long id;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(name = "cn_name", nullable = false)
    private String zhName;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Channel> channels;
}
