package com.taiger.search.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "spark_job")
public class SparkJob implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "BINARY(16)", nullable = false)
  private UUID uuid;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String path;

  @Column(nullable = false)
  private String file;

  @Column(nullable = false)
  private String mainClass;

  @Column(nullable = false)
  private int numberOfArgs;

  @OneToMany(mappedBy = "sparkJob", cascade = CascadeType.REMOVE)
  private List<Scheduler> schedulers;

  @CreatedBy
  @Column(columnDefinition = "BINARY(16)", nullable = false)
  private String createdBy;

  @CreatedDate
  @Column(nullable = false)
  private Timestamp createdDate;

  @LastModifiedBy private String modifiedBy;

  @LastModifiedDate private Timestamp modifiedDate;

  @PrePersist
  public void onPrePersist() {
      if (this.uuid == null) {
          this.uuid = UUID.randomUUID();
      }
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SparkJob sparkJob = (SparkJob) o;
      return uuid.equals(sparkJob.uuid);
  }

  @Override
  public int hashCode() {
      return Objects.hash(uuid);
  }
}
