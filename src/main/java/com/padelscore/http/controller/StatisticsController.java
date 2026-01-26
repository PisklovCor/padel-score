package com.padelscore.http.controller;

import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/tournaments/{tournamentId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable Integer tournamentId) {
        List<LeaderboardEntryDto> leaderboard = statisticsService.getLeaderboard(tournamentId);
        return ResponseEntity.ok(leaderboard);
    }
    
    @GetMapping("/tournaments/{tournamentId}/stats")
    public ResponseEntity<Map<String, Object>> getTournamentStats(@PathVariable Integer tournamentId) {
        List<LeaderboardEntryDto> leaderboard = statisticsService.getLeaderboard(tournamentId);
        
        int totalMatches = leaderboard.stream()
                .mapToInt(LeaderboardEntryDto::getMatches)
                .sum();
        
        int totalTeams = leaderboard.size();
        
        Map<String, Object> stats = Map.of(
                "tournamentId", tournamentId,
                "totalTeams", totalTeams,
                "totalMatches", totalMatches,
                "leaderboard", leaderboard
        );
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/players/{playerId}/stats")
    public ResponseEntity<Map<String, Object>> getPlayerStats(@PathVariable Integer playerId) {
        // TODO: Реализовать статистику игрока, когда будет готов PlayerService с методами статистики
        Map<String, Object> stats = Map.of(
                "playerId", playerId,
                "message", "Player statistics not implemented yet"
        );
        return ResponseEntity.ok(stats);
    }
}
