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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_players", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"team_id",
        "player_profile_id"}, name = "uk_team_players_team_player")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamPlayer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_profile_id", nullable = false)
  private PlayerProfile playerProfile;

  @Column(length = 50)
  private String position; // 'primary', 'reserve', 'coach'

  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt;

  @PrePersist
  protected void onCreate() {
    if (joinedAt == null) {
      joinedAt = LocalDateTime.now();
    }
    if (position == null) {
      position = "primary";
    }
  }
}
