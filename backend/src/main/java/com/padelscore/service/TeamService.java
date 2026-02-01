package com.padelscore.service;

import com.padelscore.dto.TeamDto;
import com.padelscore.entity.Team;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.UserRole;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamPlayerRepository;
import com.padelscore.repository.TeamRepository;
import com.padelscore.repository.TournamentRepository;
import com.padelscore.repository.UserRoleRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRoleRepository userRoleRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerProfileRepository playerProfileRepository;
    private final EntityMapper mapper;
    
    @Transactional
    public TeamDto createTeam(Integer tournamentId, String name, Long captainId, 
                              String description, String color) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        
        Team team = Team.builder()
                .tournament(tournament)
                .name(name)
                .captainId(captainId)
                .description(description)
                .color(color)
                .build();
        
        team = teamRepository.save(team);
        
        // Роль captain добавляем только если у пользователя ещё нет роли в турнире (иначе создатель турнира не смог бы быть капитаном)
        if (userRoleRepository.findByTournamentIdAndUserId(tournament.getId(), captainId).isEmpty()) {
            UserRole captainRole = UserRole.builder()
                    .tournament(tournament)
                    .userId(captainId)
                    .role("captain")
                    .build();
            userRoleRepository.save(captainRole);
        }
        
        return mapper.toDto(team);
    }
    
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByTournament(Integer tournamentId) {
        return teamRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Команды, где пользователь участвует как капитан или как игрок (по telegramId).
     */
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByUser(Long telegramId) {
        Set<Integer> seenIds = new LinkedHashSet<>();
        List<TeamDto> result = new ArrayList<>();
        for (Team team : teamRepository.findByCaptainId(telegramId)) {
            seenIds.add(team.getId());
            result.add(mapper.toDto(team));
        }
        playerProfileRepository.findByTelegramId(telegramId).ifPresent(profile -> {
            for (var tp : teamPlayerRepository.findByPlayerProfileId(profile.getId())) {
                Team team = tp.getTeam();
                if (seenIds.add(team.getId())) {
                    result.add(mapper.toDto(team));
                }
            }
        });
        return result;
    }
    
    @Transactional(readOnly = true)
    public TeamDto getTeam(Integer id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return mapper.toDto(team);
    }
    
    @Transactional
    public TeamDto updateTeam(Integer id, String name, Long captainId, 
                              String description, String color) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        if (name != null) {
            team.setName(name);
        }
        if (captainId != null) {
            team.setCaptainId(captainId);
        }
        if (description != null) {
            team.setDescription(description);
        }
        if (color != null) {
            team.setColor(color);
        }
        
        team = teamRepository.save(team);
        return mapper.toDto(team);
    }
    
    @Transactional
    public void deleteTeam(Integer id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        teamRepository.delete(team);
    }
}
