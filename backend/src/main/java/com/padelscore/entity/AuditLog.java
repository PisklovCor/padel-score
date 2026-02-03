package com.padelscore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "entity_type", nullable = false, length = 50)
  private String entityType;

  @Column(name = "entity_id", nullable = false)
  private Integer entityId;

  @Column(nullable = false, length = 50)
  private String action;

  @Column(name = "player_profile_id", nullable = false)
  private Integer playerProfileId;

  @Column(name = "old_values", columnDefinition = "jsonb")
  private String oldValues;

  @Column(name = "new_values", columnDefinition = "jsonb")
  private String newValues;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
