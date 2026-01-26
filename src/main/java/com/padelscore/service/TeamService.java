package com.padelscore.service;

import com.padelscore.dto.TeamDto;
import com.padelscore.entity.Team;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.UserRole;
import com.padelscore.repository.TeamRepository;
import com.padelscore.repository.TournamentRepository;
import com.padelscore.repository.UserRoleRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRoleRepository userRoleRepository;
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
        
        UserRole captainRole = UserRole.builder()
                .tournament(tournament)
                .userId(captainId)
                .role("captain")
                .build();
        userRoleRepository.save(captainRole);
        
        return mapper.toDto(team);
    }
    
    public List<TeamDto> getTeamsByTournament(Integer tournamentId) {
        return teamRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
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
