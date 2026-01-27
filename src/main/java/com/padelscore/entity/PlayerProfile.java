package com.padelscore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "player_profiles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"telegram_id"}, name = "uk_player_profiles_telegram_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;
    
    @Column(name = "telegram_id", unique = true)
    private Long telegramId;
    
    @Column(name = "nickname", length = 255)
    private String nickname;
    
    private Integer rating;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "playerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPlayer> teamPlayers;
    
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
