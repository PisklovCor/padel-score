package com.padelscore.service;

import com.padelscore.dto.CreateTournamentRequest;
import com.padelscore.dto.TeamDto;
import com.padelscore.dto.TournamentDto;
import com.padelscore.dto.UpdateTournamentRequest;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.UserRole;
import com.padelscore.entity.enums.TournamentStatus;
import com.padelscore.entity.enums.TournamentUserRole;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TournamentRepository;
import com.padelscore.repository.UserRoleRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    private final UserRoleRepository userRoleRepository;

    private final PlayerProfileRepository playerProfileRepository;

    private final TeamService teamService;

    private final EntityMapper mapper;
    
    @Transactional
    public TournamentDto createTournament(CreateTournamentRequest request) {
        TournamentStatus status = parseStatus(request.getStatus());
        PlayerProfile createdByProfile = playerProfileRepository.findById(
                request.getCreatedByPlayerProfileId())
                .orElseThrow(() -> new RuntimeException("Player profile not found"));
        Tournament tournament = buildTournament(request, status);
        tournament = tournamentRepository.save(tournament);
        saveAdminRole(tournament, createdByProfile);
        return mapper.toDto(tournament);
    }

    private TournamentStatus parseStatus(String status) {
        if (status == null) {
            return TournamentStatus.PLANNED;
        }
        try {
            return TournamentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TournamentStatus.PLANNED;
        }
    }

    private Tournament buildTournament(CreateTournamentRequest request, TournamentStatus status) {
        return Tournament.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdByPlayerProfileId(request.getCreatedByPlayerProfileId())
                .format(request.getFormat() != null ? request.getFormat() : "group")
                .scoringSystem(request.getScoringSystem() != null
                        ? request.getScoringSystem() : "points")
                .prize(request.getPrize())
                .status(status)
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .build();
    }

    private void saveAdminRole(Tournament tournament, PlayerProfile createdByProfile) {
        UserRole adminRole = UserRole.builder()
                .tournament(tournament)
                .playerProfile(createdByProfile)
                .role(TournamentUserRole.ADMIN)
                .build();
        userRoleRepository.save(adminRole);
    }
    
    public TournamentDto getTournament(Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return mapper.toDto(tournament);
    }
    
    public List<TournamentDto> getTournamentsByUser(Integer playerProfileId) {
        return tournamentRepository.findByCreatedByPlayerProfileId(playerProfileId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Турниры, в которых участвуют команды пользователя (как капитан или как игрок).
     */
    @Transactional(readOnly = true)
    public List<TournamentDto> getTournamentsByUserTeams(Integer playerProfileId) {
        List<TeamDto> userTeams = teamService.getTeamsByUser(playerProfileId);
        Set<Integer> tournamentIds = new LinkedHashSet<>();
        for (TeamDto team : userTeams) {
            tournamentIds.add(team.getTournamentId());
        }
        return tournamentIds.stream()
                .map(this::getTournament)
                .collect(Collectors.toList());
    }

    public boolean hasAccess(Integer playerProfileId, Integer tournamentId, String requiredRole) {
        return userRoleRepository.findByTournamentIdAndPlayerProfileId(tournamentId, playerProfileId)
                .map(role -> {
                    if (role.getRole() == TournamentUserRole.ADMIN) {
                        return true;
                    }
                    TournamentUserRole requiredRoleEnum = parseUserRole(requiredRole);
                    return requiredRoleEnum != null && requiredRoleEnum.equals(role.getRole());
                })
                .orElse(false);
    }

    /**
     * Проверяет, является ли пользователь создателем турнира.
     *
     * @param playerProfileId идентификатор профиля игрока
     * @param tournamentId идентификатор турнира
     * @return true, если пользователь является создателем турнира
     */
    @Transactional(readOnly = true)
    public boolean isTournamentCreator(Integer playerProfileId, Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return tournament.getCreatedByPlayerProfileId().equals(playerProfileId);
    }
    
    private TournamentUserRole parseUserRole(String role) {
        if (role == null) {
            return null;
        }
        try {
            return TournamentUserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TournamentDto updateTournament(Integer id, UpdateTournamentRequest request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        applyTournamentUpdates(tournament, request);
        tournament = tournamentRepository.save(tournament);
        return mapper.toDto(tournament);
    }

    private void applyTournamentUpdates(Tournament tournament, UpdateTournamentRequest request) {
        applyIfPresent(request.getTitle(), tournament::setTitle);
        applyIfPresent(request.getDescription(), tournament::setDescription);
        applyIfPresent(request.getStartDate(), tournament::setStartDate);
        applyIfPresent(request.getEndDate(), tournament::setEndDate);
        applyIfPresent(request.getFormat(), tournament::setFormat);
        applyIfPresent(request.getScoringSystem(), tournament::setScoringSystem);
        applyIfPresent(request.getPrize(), tournament::setPrize);
        if (request.getStatus() != null) {
            tournament.setStatus(parseAndValidateStatus(request.getStatus()));
        }
        applyIfPresent(request.getCompleted(), tournament::setCompleted);
    }

    private <T> void applyIfPresent(T value, java.util.function.Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    private TournamentStatus parseAndValidateStatus(String status) {
        try {
            return TournamentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid tournament status: " + status);
        }
    }
    
    @Transactional
    public void deleteTournament(Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournamentRepository.delete(tournament);
    }
}
