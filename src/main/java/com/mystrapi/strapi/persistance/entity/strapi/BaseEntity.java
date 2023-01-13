package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author tangqiang
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
    @Column(name = "update_time", columnDefinition = "DATETIME", nullable = false)
    @LastModifiedDate
    private LocalDateTime updateTime;

    @Column(name = "create_time", columnDefinition = "DATETIME", nullable = false)
    @CreatedDate
    private LocalDateTime createTime;

    @Generated(GenerationTime.INSERT)
    @ColumnDefault("\"system\"")
    @LastModifiedBy
    @Column(name = "update_by", insertable = false, updatable = false)
    private String updateBy;

    @Generated(GenerationTime.INSERT)
    @ColumnDefault("\"system\"")
    @CreatedBy
    @Column(name = "create_by", insertable = false, updatable = false)
    private String createBy;
}
