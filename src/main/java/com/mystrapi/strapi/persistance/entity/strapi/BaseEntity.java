package com.mystrapi.strapi.persistance.entity.strapi;

import jakarta.persistence.*;
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
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", columnDefinition = "DATETIME")
    private LocalDateTime updateTime;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", columnDefinition = "DATETIME")
    private LocalDateTime createTime;

    @LastModifiedBy
    @Column(name = "update_by")
    private String updateBy;

    @CreatedBy
    @Column(name = "create_by")
    private String createBy;
}
