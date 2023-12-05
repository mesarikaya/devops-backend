package com.mes.devops.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@SuperBuilder()
@Data
@MappedSuperclass
public class Audit implements Serializable {

    @Serial
    private final static long serialVersionUID = -3836806622675683599L;

    @UpdateTimestamp
    @Column(name="update_date")
    private LocalDateTime updateDate;

    @CreationTimestamp
    @Column(name="creation_date")
    private LocalDateTime creationDate;
}
