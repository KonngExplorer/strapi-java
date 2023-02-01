package com.mystrapi.strapi.persistance.entity.strapi;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "strapi_menu")
@ToString(exclude = {"authority"})
public class Menu extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_menu_pk")
    @GenericGenerator(name = "strapi_menu_pk", strategy = "identity")
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "strapi_authority_menu", joinColumns = @JoinColumn(name = "menu_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Authority authority;
}
