package com.padelscore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "set_scores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "set_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
    
    @Column(name = "set_number", nullable = false)
    private Integer setNumber;
    
    @Column(name = "team1_games", nullable = false)
    private Integer team1Games;
    
    @Column(name = "team2_games", nullable = false)
    private Integer team2Games;
    
    @Column(name = "tiebreaker_team1")
    private Integer tiebreakerTeam1;
    
    @Column(name = "tiebreaker_team2")
    private Integer tiebreakerTeam2;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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
