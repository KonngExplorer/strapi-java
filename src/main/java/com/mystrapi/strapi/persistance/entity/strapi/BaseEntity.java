package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * @author tangqiang
 */
@MappedSuperclass
public class BaseEntity {
    @Basic(optional = false)
    @Column(name = "updateTime", columnDefinition = "DATETIME")
    @UpdateTimestamp
    private LocalDateTime updateTime;

    @Basic(optional = false)
    @Column(name = "createTime", columnDefinition = "DATETIME")
    @CreationTimestamp
    private LocalDateTime createTime;

    @Basic(optional = false)
    private String updateBy;
    @Basic(optional = false)
    private String createBy;
}
