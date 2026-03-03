package com.padelscore.service;

import com.padelscore.dto.CreatePlayerRequest;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
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
    public TeamPlayerDto createPlayer(CreatePlayerRequest request) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));
        TeamPlayer teamPlayer = findOrCreateProfileAndLink(team, request);
        return mapper.toDto(teamPlayer);
    }

    private TeamPlayer findOrCreateProfileAndLink(Team team, CreatePlayerRequest request) {
        TeamPlayer byTelegram = tryLinkByTelegramId(team, request);
        if (byTelegram != null) {
            return byTelegram;
        }
        TeamPlayer byName = tryLinkByName(team, request);
        if (byName != null) {
            return byName;
        }
        return createNewProfileAndLink(team, request);
    }

    private TeamPlayer tryLinkByTelegramId(Team team, CreatePlayerRequest request) {
        if (request.getTelegramId() == null) {
            return null;
        }
        Optional<PlayerProfile> existing = playerProfileRepository.findByTelegramId(
                request.getTelegramId());
        if (existing.isEmpty()) {
            return null;
        }
        if (teamPlayerRepository.existsByTeamIdAndPlayerProfileId(team.getId(),
                existing.get().getId())) {
            throw new RuntimeException("Player already exists in this team");
        }
        return saveTeamPlayer(team, existing.get(), request.getPosition());
    }

    private TeamPlayer tryLinkByName(Team team, CreatePlayerRequest request) {
        if (request.getTelegramId() != null) {
            return null;
        }
        List<PlayerProfile> profiles = playerProfileRepository.findByFirstNameAndLastName(
                request.getFirstName(), request.getLastName());
        for (PlayerProfile profile : profiles) {
            if (!teamPlayerRepository.existsByTeamIdAndPlayerProfileId(team.getId(),
                    profile.getId())) {
                return saveTeamPlayer(team, profile, request.getPosition());
            }
        }
        return null;
    }

    private TeamPlayer createNewProfileAndLink(Team team, CreatePlayerRequest request) {
        PlayerProfile profile = PlayerProfile.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .telegramId(request.getTelegramId())
                .rating(request.getRating())
                .build();
        profile = playerProfileRepository.save(profile);
        return saveTeamPlayer(team, profile, request.getPosition());
    }

    private TeamPlayer saveTeamPlayer(Team team, PlayerProfile profile, String position) {
        TeamPlayer teamPlayer = TeamPlayer.builder()
                .team(team)
                .playerProfile(profile)
                .position(parsePosition(position))
                .build();
        return teamPlayerRepository.save(teamPlayer);
    }
    
    @Transactional
    public TeamPlayerDto updatePlayer(Integer id, UpdatePlayerRequest request) {
        TeamPlayer teamPlayer = teamPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        return mapper.toDto(applyPlayerUpdates(teamPlayer, request));
    }

    private TeamPlayer applyPlayerUpdates(TeamPlayer teamPlayer, UpdatePlayerRequest request) {
        PlayerProfile profile = teamPlayer.getPlayerProfile();
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getTelegramId() != null) {
            profile.setTelegramId(request.getTelegramId());
        }
        if (request.getRating() != null) {
            profile.setRating(request.getRating());
        }
        playerProfileRepository.save(profile);
        if (request.getPosition() != null) {
            teamPlayer.setPosition(parsePosition(request.getPosition()));
            teamPlayer = teamPlayerRepository.save(teamPlayer);
        }
        return teamPlayer;
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
