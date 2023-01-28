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
@Table(name = "strapi_channel")
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

    @Column(name = "abstact")
    private String abstact;
}
