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
@Table(name = "strapi_channel")
@ToString(exclude = {"site", "documents"})
public class Channel extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "strapi_channel_pk")
    @GenericGenerator(name = "strapi_channel_pk", strategy = "identity")
    private Long id;

    @Column(name = "channel_name", nullable = false)
    private String channelName;

    @Column(name = "cn_name", nullable = false)
    private String zhName;

    @Column(name = "description")
    private String description;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @JsonBackReference
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Document> documents;
}
