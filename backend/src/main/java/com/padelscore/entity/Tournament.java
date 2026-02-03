package com.padelscore.entity;

import com.padelscore.entity.enums.TournamentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_by_player_profile_id", nullable = false)
  private Integer createdByPlayerProfileId;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(length = 50)
  private String format;

  @Column(name = "scoring_system", length = 50)
  private String scoringSystem;

  @Column(columnDefinition = "TEXT")
  private String prize;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private TournamentStatus status;

  @Column(nullable = false)
  private Boolean completed = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Team> teams;

  @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Match> matches;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
