package com.mes.devops.domain;

import com.mes.devops.document.FileMetadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@SuperBuilder
@Entity
@Table(name="files", schema="file_management")
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class File extends Audit implements Serializable {

    @Serial
    private static final long serialVersionUID = -2147372509825504534L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "FILE_SEQUENCE_GENERATOR")
    @Column(name="id")
    private Long id;

    @Column(name="uuid")
    private UUID uuid;

    @Column(name="name")
    private String name;

    @Column(name="type")
    private String type;

    @Column(name="size")
    private Long size;

    @Column(name="storage_path")
    private String storagePath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="metadata", columnDefinition = "jsonb")
    private FileMetadata metadata;

    @Column(name="visibility")
    private final String visibility;

    @ManyToOne
    @JoinColumn(name="status_id")
    private Status status;
}
