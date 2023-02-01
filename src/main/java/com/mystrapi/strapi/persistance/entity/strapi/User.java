package com.mystrapi.strapi.persistance.entity.strapi;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "strapi_user")
@ToString(exclude = {"authorities", "group"})
public class User extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_user_pk")
    @GenericGenerator(name = "strapi_user_pk", strategy = "identity")
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "enabled", nullable = false, length = 1)
    private Boolean enabled;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "strapi_user_authority", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "strapi_user_group", joinColumns = @JoinColumn(name = "user_id"))
    private Group group;

}
