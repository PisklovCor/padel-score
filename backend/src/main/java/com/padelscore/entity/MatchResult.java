package com.padelscore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id", nullable = false, unique = true)
  private Match match;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "winner_team_id", nullable = false)
  private Team winnerTeam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loser_team_id", nullable = false)
  private Team loserTeam;

  @Column(name = "winner_points")
  private Integer winnerPoints;

  @Column(name = "loser_points")
  private Integer loserPoints;

  @Column(name = "final_score", length = 50)
  private String finalScore;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Column(name = "submitted_by_player_profile_id", nullable = false)
  private Integer submittedByPlayerProfileId;

  @Column(name = "submitted_at")
  private LocalDateTime submittedAt;

  @Column(nullable = false)
  private Boolean disputed;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (submittedAt == null) {
      submittedAt = LocalDateTime.now();
    }
    if (disputed == null) {
      disputed = false;
    }
    if (winnerPoints == null) {
      winnerPoints = 3;
    }
    if (loserPoints == null) {
      loserPoints = 1;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
