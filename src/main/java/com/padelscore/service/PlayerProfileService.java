package com.padelscore.service;

import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamPlayerRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {
    
    private final PlayerProfileRepository playerProfileRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final EntityMapper mapper;
    
    public PlayerProfileDto getPlayerProfile(Integer id) {
        PlayerProfile profile = playerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        return mapper.toDto(profile);
    }
    
    public List<PlayerProfileDto> getAllPlayerProfiles() {
        return playerProfileRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<PlayerProfileDto> searchPlayerProfiles(String searchTerm) {
        return playerProfileRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    public PlayerProfileDto getPlayerProfileByTelegramId(Long telegramId) {
        PlayerProfile profile = playerProfileRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        return mapper.toDto(profile);
    }
    
    @Transactional
    public PlayerProfileDto createPlayerProfile(String firstName, String lastName, String nickname, Long telegramId, Integer rating) {
        if (telegramId != null && playerProfileRepository.findByTelegramId(telegramId).isPresent()) {
            throw new RuntimeException("Player profile with this telegram_id already exists");
        }
        
        if (nickname != null && !nickname.trim().isEmpty()) {
            playerProfileRepository.findByNicknameIgnoreCase(nickname).ifPresent(existing -> {
                throw new RuntimeException("Player profile with this nickname already exists");
            });
        }
        
        PlayerProfile profile = PlayerProfile.builder()
                .firstName(firstName)
                .lastName(lastName)
                .nickname(nickname)
                .telegramId(telegramId)
                .rating(rating)
                .build();
        
        profile = playerProfileRepository.save(profile);
        return mapper.toDto(profile);
    }
    
    @Transactional
    public PlayerProfileDto updatePlayerProfile(Integer id, String firstName, String lastName,
                                         String nickname, Long telegramId, Integer rating) {
        PlayerProfile profile = playerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        
        if (firstName != null) {
            profile.setFirstName(firstName);
        }
        if (lastName != null) {
            profile.setLastName(lastName);
        }
        if (nickname != null) {
            // Проверяем уникальность nickname (регистронезависимо)
            String currentNickname = profile.getNickname();
            boolean nicknameChanged = currentNickname == null || !currentNickname.equalsIgnoreCase(nickname);
            if (nicknameChanged && !nickname.trim().isEmpty()) {
                playerProfileRepository.findByNicknameIgnoreCase(nickname).ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("Player profile with this nickname already exists");
                    }
                });
            }
            profile.setNickname(nickname.trim().isEmpty() ? null : nickname);
        }
        if (telegramId != null) {
            // Проверяем уникальность telegram_id
            if (!telegramId.equals(profile.getTelegramId())) {
                playerProfileRepository.findByTelegramId(telegramId).ifPresent(existing -> {
                    throw new RuntimeException("Player profile with this telegram_id already exists");
                });
            }
            profile.setTelegramId(telegramId);
        }
        if (rating != null) {
            profile.setRating(rating);
        }
        
        profile = playerProfileRepository.save(profile);
        return mapper.toDto(profile);
    }
    
    @Transactional
    public void deletePlayerProfile(Integer id) {
        PlayerProfile profile = playerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        
        // Проверяем, не состоит ли игрок в командах
        if (!teamPlayerRepository.findByPlayerProfileId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete player profile: player is still in teams");
        }
        
        playerProfileRepository.delete(profile);
    }
    
    public List<TeamPlayerDto> getPlayerTeams(Integer playerProfileId) {
        return teamPlayerRepository.findByPlayerProfileId(playerProfileId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
