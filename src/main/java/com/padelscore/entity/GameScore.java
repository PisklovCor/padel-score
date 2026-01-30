package com.padelscore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_scores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"set_id", "game_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameScore {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "set_id", nullable = false)
  private SetScore setScore;

  @Column(name = "game_number", nullable = false)
  private Integer gameNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "server_team_id", nullable = false)
  private Team serverTeam;

  @Column(name = "team1_points", nullable = false)
  private Integer team1Points;

  @Column(name = "team2_points", nullable = false)
  private Integer team2Points;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (team1Points == null) {
      team1Points = 0;
    }
    if (team2Points == null) {
      team2Points = 0;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
