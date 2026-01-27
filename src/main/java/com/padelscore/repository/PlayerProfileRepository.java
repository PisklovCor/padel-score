package com.padelscore.repository;

import com.padelscore.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Integer> {
    Optional<PlayerProfile> findByTelegramId(Long telegramId);
    Optional<PlayerProfile> findByNicknameIgnoreCase(String nickname);
    List<PlayerProfile> findByFirstNameAndLastName(String firstName, String lastName);
    List<PlayerProfile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
