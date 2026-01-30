package com.padelscore.repository;

import com.padelscore.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Integer> {
    boolean existsByTelegramId(Long telegramId);
    Optional<PlayerProfile> findByTelegramId(Long telegramId);
    Optional<PlayerProfile> findByNicknameIgnoreCase(String nickname);
    Optional<PlayerProfile> findByFirstNameAndTelegramId(String firstName, Long telegramId);
    List<PlayerProfile> findByFirstNameAndLastName(String firstName, String lastName);
    List<PlayerProfile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
