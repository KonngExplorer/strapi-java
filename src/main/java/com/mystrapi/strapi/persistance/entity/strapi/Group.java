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
@Table(name = "strapi_group")
@ToString(exclude = {"users", "users", "authorities"})
public class Group extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_group_pk")
    @GenericGenerator(name = "strapi_group_pk", strategy = "identity")
    private Long id;

    @Column(name = "user_group", unique = true, nullable = false)
    private String group;

    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "strapi_user_group", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "strapi_group_authority", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities;
}
