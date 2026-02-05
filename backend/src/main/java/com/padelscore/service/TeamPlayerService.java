package com.padelscore.service;

import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.entity.Team;
import com.padelscore.entity.TeamPlayer;
import com.padelscore.entity.enums.TeamPlayerPosition;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamPlayerRepository;
import com.padelscore.repository.TeamRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamPlayerService {
    
    private final TeamPlayerRepository teamPlayerRepository;

    private final TeamRepository teamRepository;

    private final PlayerProfileRepository playerProfileRepository;

    private final EntityMapper mapper;
    
    @Transactional(readOnly = true)
    public List<TeamPlayerDto> getPlayersByTeam(Integer teamId) {
        return teamPlayerRepository.findByTeamId(teamId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TeamPlayerDto getTeamPlayer(Integer id) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team player not found"));
        return mapper.toDto(teamPlayer);
    }
    
    @Transactional
    public TeamPlayerDto addPlayerToTeam(Integer teamId, Integer playerProfileId, String position) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        PlayerProfile profile = playerProfileRepository.findById(playerProfileId)
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        
        // Проверяем, не добавлен ли уже игрок в команду
        if (teamPlayerRepository.existsByTeamIdAndPlayerProfileId(teamId, playerProfileId)) {
            throw new RuntimeException("Player already exists in this team");
        }
        
        TeamPlayerPosition playerPosition = parsePosition(position);
        
        TeamPlayer teamPlayer = TeamPlayer.builder()
                .team(team)
                .playerProfile(profile)
                .position(playerPosition)
                .build();
        
        teamPlayer = teamPlayerRepository.save(teamPlayer);
        return mapper.toDto(teamPlayer);
    }
    
    @Transactional
    public TeamPlayerDto updateTeamPlayer(Integer id, String position) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team player not found"));
        
        if (position != null) {
            teamPlayer.setPosition(parsePosition(position));
            teamPlayer = teamPlayerRepository.save(teamPlayer);
        }
        
        return mapper.toDto(teamPlayer);
    }
    
    @Transactional
    public void removePlayerFromTeam(Integer teamId, Integer playerProfileId) {
        TeamPlayer teamPlayer = teamPlayerRepository.findByTeamIdAndPlayerProfileId(teamId, playerProfileId)
                .orElseThrow(() -> new RuntimeException("Player not found in this team"));
        teamPlayerRepository.delete(teamPlayer);
    }
    
    @Transactional
    public void deleteTeamPlayer(Integer id) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team player not found"));
        teamPlayerRepository.delete(teamPlayer);
    }
    
    @Transactional
    public TeamPlayerDto createPlayer(Integer teamId, String firstName, String lastName, 
                                 Long telegramId, Integer rating, String position) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        // Проверяем, не добавлен ли уже этот игрок в команду
        if (telegramId != null) {
            Optional<PlayerProfile> existingProfile = playerProfileRepository.findByTelegramId(telegramId);
            if (existingProfile.isPresent()) {
                // Проверяем, не состоит ли уже в этой команде
                if (teamPlayerRepository.existsByTeamIdAndPlayerProfileId(teamId, existingProfile.get().getId())) {
                    throw new RuntimeException("Player already exists in this team");
                }
                // Создаем связь с существующим профилем
                TeamPlayerPosition playerPosition = parsePosition(position);
                TeamPlayer teamPlayer = TeamPlayer.builder()
                        .team(team)
                        .playerProfile(existingProfile.get())
                        .position(playerPosition)
                        .build();
                teamPlayer = teamPlayerRepository.save(teamPlayer);
                return mapper.toDto(teamPlayer);
            }
        }
        
        // Ищем по имени, если telegram_id не указан
        if (telegramId == null) {
            List<PlayerProfile> profilesByName = playerProfileRepository.findByFirstNameAndLastName(firstName, lastName);
            for (PlayerProfile profile : profilesByName) {
                if (!teamPlayerRepository.existsByTeamIdAndPlayerProfileId(teamId, profile.getId())) {
                    // Найден профиль с таким именем, создаем связь
                    TeamPlayerPosition playerPosition = parsePosition(position);
                    TeamPlayer teamPlayer = TeamPlayer.builder()
                            .team(team)
                            .playerProfile(profile)
                            .position(playerPosition)
                            .build();
                    teamPlayer = teamPlayerRepository.save(teamPlayer);
                    return mapper.toDto(teamPlayer);
                }
            }
        }
        
        // Создаем новый профиль игрока
        PlayerProfile profile = PlayerProfile.builder()
                .firstName(firstName)
                .lastName(lastName)
                .telegramId(telegramId)
                .rating(rating)
                .build();
        profile = playerProfileRepository.save(profile);
        
        // Создаем связь с командой
        TeamPlayerPosition playerPosition = parsePosition(position);
        TeamPlayer teamPlayer = TeamPlayer.builder()
                .team(team)
                .playerProfile(profile)
                .position(playerPosition)
                .build();
        teamPlayer = teamPlayerRepository.save(teamPlayer);
        
        return mapper.toDto(teamPlayer);
    }
    
    @Transactional
    public TeamPlayerDto updatePlayer(Integer id, String firstName, String lastName,
                                  Long telegramId, Integer rating, String position) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        PlayerProfile profile = teamPlayer.getPlayerProfile();
        
        // Обновляем профиль игрока (изменения применятся ко всем командам)
        if (firstName != null) {
            profile.setFirstName(firstName);
        }
        if (lastName != null) {
            profile.setLastName(lastName);
        }
        if (telegramId != null) {
            profile.setTelegramId(telegramId);
        }
        if (rating != null) {
            profile.setRating(rating);
        }
        profile = playerProfileRepository.save(profile);
        
        // Обновляем позицию в конкретной команде
        if (position != null) {
            teamPlayer.setPosition(parsePosition(position));
            teamPlayer = teamPlayerRepository.save(teamPlayer);
        }
        
        return mapper.toDto(teamPlayer);
    }
    
    private TeamPlayerPosition parsePosition(String position) {
        if (position == null) {
            return TeamPlayerPosition.PRIMARY;
        }
        try {
            return TeamPlayerPosition.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TeamPlayerPosition.PRIMARY;
        }
    }
}
