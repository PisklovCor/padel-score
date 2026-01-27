package com.padelscore.service;

import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.entity.Match;
import com.padelscore.entity.MatchResult;
import com.padelscore.entity.Team;
import com.padelscore.repository.MatchRepository;
import com.padelscore.repository.MatchResultRepository;
import com.padelscore.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    
    public List<LeaderboardEntryDto> getLeaderboard(Integer tournamentId) {
        List<Team> teams = teamRepository.findByTournamentId(tournamentId);
        List<Match> completedMatches = matchRepository.findByTournamentIdAndStatus(tournamentId, "completed");
        
        Map<Integer, LeaderboardEntryDto> leaderboard = new HashMap<>();
        
        for (Team team : teams) {
            LeaderboardEntryDto entry = LeaderboardEntryDto.builder()
                    .teamId(team.getId())
                    .teamName(team.getName())
                    .matches(0)
                    .wins(0)
                    .losses(0)
                    .setsWon(0)
                    .setsLost(0)
                    .gamesWon(0)
                    .gamesLost(0)
                    .points(0)
                    .winRate(0.0)
                    .build();
            leaderboard.put(team.getId(), entry);
        }
        
        for (Match match : completedMatches) {
            matchResultRepository.findByMatchId(match.getId()).ifPresent(result -> {
                LeaderboardEntryDto winner = leaderboard.get(result.getWinnerTeam().getId());
                LeaderboardEntryDto loser = leaderboard.get(result.getLoserTeam().getId());
                
                if (winner != null && loser != null) {
                    winner.setMatches(winner.getMatches() + 1);
                    winner.setWins(winner.getWins() + 1);
                    winner.setPoints(winner.getPoints() + result.getWinnerPoints());
                    
                    loser.setMatches(loser.getMatches() + 1);
                    loser.setLosses(loser.getLosses() + 1);
                    loser.setPoints(loser.getPoints() + result.getLoserPoints());
                    
                    String[] scores = result.getFinalScore().split("-");
                    if (scores.length == 2) {
                        int winnerSets = Integer.parseInt(scores[0].trim());
                        int loserSets = Integer.parseInt(scores[1].trim());
                        
                        if (result.getWinnerTeam().getId().equals(match.getTeam1().getId())) {
                            winner.setSetsWon(winner.getSetsWon() + winnerSets);
                            winner.setSetsLost(winner.getSetsLost() + loserSets);
                            loser.setSetsWon(loser.getSetsWon() + loserSets);
                            loser.setSetsLost(loser.getSetsLost() + winnerSets);
                        } else {
                            winner.setSetsWon(winner.getSetsWon() + loserSets);
                            winner.setSetsLost(winner.getSetsLost() + winnerSets);
                            loser.setSetsWon(loser.getSetsWon() + winnerSets);
                            loser.setSetsLost(loser.getSetsLost() + loserSets);
                        }
                    }
                }
            });
        }
        
        for (LeaderboardEntryDto entry : leaderboard.values()) {
            if (entry.getMatches() > 0) {
                entry.setWinRate((double) entry.getWins() / entry.getMatches());
            }
        }
        
        return leaderboard.values().stream()
                .sorted((a, b) -> {
                    int pointsCompare = Integer.compare(b.getPoints(), a.getPoints());
                    if (pointsCompare != 0) return pointsCompare;
                    int setsDiffCompare = Integer.compare(
                            b.getSetsWon() - b.getSetsLost(),
                            a.getSetsWon() - a.getSetsLost());
                    if (setsDiffCompare != 0) return setsDiffCompare;
                    return Integer.compare(
                            b.getGamesWon() - b.getGamesLost(),
                            a.getGamesWon() - a.getGamesLost());
                })
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getTournamentStats(Integer tournamentId) {
        List<LeaderboardEntryDto> leaderboard = getLeaderboard(tournamentId);
        
        int totalMatches = leaderboard.stream()
                .mapToInt(LeaderboardEntryDto::getMatches)
                .sum();
        
        int totalTeams = leaderboard.size();
        
        return Map.of(
                "tournamentId", tournamentId,
                "totalTeams", totalTeams,
                "totalMatches", totalMatches,
                "leaderboard", leaderboard
        );
    }
}
