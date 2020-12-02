package com.taiger.search.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
@Table(name = "scheduler")
public class Scheduler implements Serializable {

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
  private String args;

  @Column(nullable = false)
  private String scheduleInterval;

  @Column(nullable = false)
  private String driverMemory;

  @Column(nullable = false)
  private int executorCores;

  @Column(nullable = false)
  private int retries;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String dag;

  @Column(nullable = false)
  private boolean active;

  @CreatedDate
  @Column(nullable = false)
  private Timestamp startDate;

  @ManyToOne(fetch = FetchType.LAZY)
  private SparkJob sparkJob;

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
      if (this.dag == null) {
    	  this.dag = this.name.replaceAll(" ", "") + this.uuid;
      }
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Scheduler scheduler = (Scheduler) o;
      return uuid.equals(scheduler.uuid);
  }

  @Override
  public int hashCode() {
      return Objects.hash(uuid);
  }
}
