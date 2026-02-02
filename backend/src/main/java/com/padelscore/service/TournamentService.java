package com.padelscore.service;

import com.padelscore.dto.TeamDto;
import com.padelscore.dto.TournamentDto;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.UserRole;
import com.padelscore.entity.enums.TournamentStatus;
import com.padelscore.repository.TournamentRepository;
import com.padelscore.repository.UserRoleRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRoleRepository userRoleRepository;
    private final TeamService teamService;
    private final EntityMapper mapper;
    
    @Transactional
    public TournamentDto createTournament(String title, String description, Long createdBy, 
                                         String format, String scoringSystem, String prize,
                                         String status, Boolean completed) {
        TournamentStatus tournamentStatus = null;
        if (status != null) {
            try {
                tournamentStatus = TournamentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                tournamentStatus = TournamentStatus.PLANNED;
            }
        } else {
            tournamentStatus = TournamentStatus.PLANNED;
        }
        
        Tournament tournament = Tournament.builder()
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .format(format != null ? format : "group")
                .scoringSystem(scoringSystem != null ? scoringSystem : "points")
                .prize(prize)
                .status(tournamentStatus)
                .completed(completed != null ? completed : false)
                .build();
        
        tournament = tournamentRepository.save(tournament);
        
        UserRole adminRole = UserRole.builder()
                .tournament(tournament)
                .userId(createdBy)
                .role("admin")
                .build();
        userRoleRepository.save(adminRole);
        
        return mapper.toDto(tournament);
    }
    
    public TournamentDto getTournament(Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return mapper.toDto(tournament);
    }
    
    public List<TournamentDto> getTournamentsByUser(Long userId) {
        return tournamentRepository.findByCreatedBy(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Турниры, в которых участвуют команды пользователя (как капитан или как игрок).
     */
    @Transactional(readOnly = true)
    public List<TournamentDto> getTournamentsByUserTeams(Long userId) {
        List<TeamDto> userTeams = teamService.getTeamsByUser(userId);
        Set<Integer> tournamentIds = new LinkedHashSet<>();
        for (TeamDto team : userTeams) {
            tournamentIds.add(team.getTournamentId());
        }
        return tournamentIds.stream()
                .map(this::getTournament)
                .collect(Collectors.toList());
    }

    public boolean hasAccess(Long userId, Integer tournamentId, String requiredRole) {
        return userRoleRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .map(role -> {
                    if ("admin".equals(role.getRole())) {
                        return true;
                    }
                    return requiredRole.equals(role.getRole());
                })
                .orElse(false);
    }
    
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TournamentDto updateTournament(Integer id, String title, String description,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         String format, String scoringSystem, String prize,
                                         String status, Boolean completed) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        
        if (title != null) {
            tournament.setTitle(title);
        }
        if (description != null) {
            tournament.setDescription(description);
        }
        if (startDate != null) {
            tournament.setStartDate(startDate);
        }
        if (endDate != null) {
            tournament.setEndDate(endDate);
        }
        if (format != null) {
            tournament.setFormat(format);
        }
        if (scoringSystem != null) {
            tournament.setScoringSystem(scoringSystem);
        }
        if (prize != null) {
            tournament.setPrize(prize);
        }
        if (status != null) {
            try {
                tournament.setStatus(TournamentStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid tournament status: " + status);
            }
        }
        if (completed != null) {
            tournament.setCompleted(completed);
        }
        
        tournament = tournamentRepository.save(tournament);
        return mapper.toDto(tournament);
    }
    
    @Transactional
    public void deleteTournament(Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournamentRepository.delete(tournament);
    }
}
