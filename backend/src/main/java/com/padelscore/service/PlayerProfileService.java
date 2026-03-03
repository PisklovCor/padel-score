package com.padelscore.service;

import com.padelscore.dto.CreatePlayerProfileRequest;
import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.exception.NicknameNotUniqueException;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamPlayerRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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

  private final JdbcTemplate jdbcTemplate;

  public boolean existsByTelegramId(Long telegramId) {
    return playerProfileRepository.existsByTelegramId(telegramId);
  }

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
  public PlayerProfileDto createPlayerProfile(CreatePlayerProfileRequest request) {
    validateCreateUniqueness(request.getFirstName(), request.getNickname(),
        request.getTelegramId());
    int initialRating = request.getRating() != null ? request.getRating() : 500;
    String lastName = normalizeLastName(request.getLastName());
    PlayerProfile profile = PlayerProfile.builder()
        .firstName(request.getFirstName())
        .lastName(lastName)
        .nickname(request.getNickname())
        .telegramId(request.getTelegramId())
        .rating(initialRating)
        .build();
    profile = playerProfileRepository.save(profile);
    return mapper.toDto(profile);
  }

  private void validateCreateUniqueness(String firstName, String nickname, Long telegramId) {
    if (telegramId != null && playerProfileRepository.findByTelegramId(telegramId).isPresent()) {
      throw new RuntimeException("Player profile with this telegram_id already exists");
    }
    if (telegramId != null && playerProfileRepository.findByFirstNameAndTelegramId(firstName,
        telegramId).isPresent()) {
      throw new RuntimeException(
          "Player profile with this first_name and telegram_id already exists");
    }
    if (nickname != null && !nickname.trim().isEmpty()) {
      playerProfileRepository.findByNicknameIgnoreCase(nickname.trim()).ifPresent(existing -> {
        throw new NicknameNotUniqueException("Player profile with this nickname already exists");
      });
    }
  }

  private static String normalizeLastName(String lastName) {
    return lastName != null && !lastName.trim().isEmpty() ? lastName : null;
  }

  @Transactional
  public PlayerProfileDto updatePlayerProfile(Integer id, UpdatePlayerRequest request) {
    PlayerProfile profile = playerProfileRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Player profile not found"));
    applyProfileUpdates(profile, id, request);
    validateUpdateUniqueness(profile, id, request);
    profile = playerProfileRepository.save(profile);
    return mapper.toDto(profile);
  }

  private void applyProfileUpdates(PlayerProfile profile, Integer id, UpdatePlayerRequest request) {
    if (request.getFirstName() != null) {
      profile.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      profile.setLastName(request.getLastName().trim().isEmpty() ? null : request.getLastName());
    }
    if (request.getNickname() != null) {
      validateNicknameUniqueness(id, request.getNickname(), profile.getNickname());
      profile.setNickname(request.getNickname().trim().isEmpty() ? null : request.getNickname());
    }
    if (request.getTelegramId() != null) {
      validateTelegramIdUniqueness(id, request.getTelegramId(), profile.getTelegramId());
      profile.setTelegramId(request.getTelegramId());
    }
    if (request.getRating() != null) {
      profile.setRating(request.getRating());
    }
  }

  private void validateNicknameUniqueness(Integer id, String nickname, String currentNickname) {
    boolean nicknameChanged = currentNickname == null || !currentNickname.equalsIgnoreCase(nickname);
    if (!nicknameChanged || nickname.trim().isEmpty()) {
      return;
    }
    playerProfileRepository.findByNicknameIgnoreCase(nickname.trim()).ifPresent(existing -> {
      if (!existing.getId().equals(id)) {
        throw new NicknameNotUniqueException(
            "Player profile with this nickname already exists");
      }
    });
  }

  private void validateTelegramIdUniqueness(Integer id, Long telegramId, Long currentTelegramId) {
    if (telegramId.equals(currentTelegramId)) {
      return;
    }
    playerProfileRepository.findByTelegramId(telegramId).ifPresent(existing -> {
      throw new RuntimeException("Player profile with this telegram_id already exists");
    });
  }

  private void validateUpdateUniqueness(PlayerProfile profile, Integer id,
      UpdatePlayerRequest request) {
    String finalFirstName = request.getFirstName() != null
        ? request.getFirstName() : profile.getFirstName();
    Long finalTelegramId = request.getTelegramId() != null
        ? request.getTelegramId() : profile.getTelegramId();
    if (finalTelegramId == null) {
      return;
    }
    playerProfileRepository.findByFirstNameAndTelegramId(finalFirstName, finalTelegramId)
        .ifPresent(existing -> {
          if (!existing.getId().equals(id)) {
            throw new RuntimeException(
                "Player profile with this first_name and telegram_id already exists");
          }
        });
  }

  /**
   * Удаляет профиль игрока. Гибридный подход: ссылки на player_profiles без FK, поэтому удаление не
   * блокируется БД. Турниры/команды/результаты остаются с ID удалённого профиля; при загрузке связь
   * будет null, в DTO — ID или null.
   */
  @Transactional
  public void deletePlayerProfile(Integer id) {
    jdbcTemplate.update("SET LOCAL app.player_profile_id = " + id);

    PlayerProfile profile = playerProfileRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Player profile not found"));
    playerProfileRepository.delete(profile);
  }

  public List<TeamPlayerDto> getPlayerTeams(Integer playerProfileId) {
    return teamPlayerRepository.findByPlayerProfileId(playerProfileId).stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());
  }
}
