package com.mes.devops.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;

@SuperBuilder
@Entity
@Table(name = "Statuses", schema="file_management")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class Status extends Audit implements Serializable {

    @Serial
    private static final long serialVersionUID = -3799029117850795972L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "FILE_SEQUENCE_GENERATOR")
    @Column(name="id")
    private Long id;

    @Column(name="type")
    private String type;

    @Column(name="description")
    private String description;
}
