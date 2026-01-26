package com.padelscore.service;

import com.padelscore.dto.TournamentDto;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.UserRole;
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
public class TournamentService {
    
    private final TournamentRepository tournamentRepository;
    private final UserRoleRepository userRoleRepository;
    private final EntityMapper mapper;
    
    @Transactional
    public TournamentDto createTournament(String title, String description, Long createdBy, 
                                         String format, String scoringSystem) {
        Tournament tournament = Tournament.builder()
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .format(format != null ? format : "group")
                .scoringSystem(scoringSystem != null ? scoringSystem : "points")
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
}
