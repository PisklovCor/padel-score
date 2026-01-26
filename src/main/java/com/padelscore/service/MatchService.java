package com.padelscore.service;

import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.entity.*;
import com.padelscore.repository.*;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final EntityMapper mapper;
    
    @Transactional
    public MatchDto createMatch(Integer tournamentId, Integer team1Id, Integer team2Id, 
                                LocalDateTime scheduledDate, String format) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        Team team1 = teamRepository.findById(team1Id)
                .orElseThrow(() -> new RuntimeException("Team 1 not found"));
        Team team2 = teamRepository.findById(team2Id)
                .orElseThrow(() -> new RuntimeException("Team 2 not found"));
        
        if (team1Id.equals(team2Id)) {
            throw new RuntimeException("Team 1 and Team 2 cannot be the same");
        }
        
        Match match = Match.builder()
                .tournament(tournament)
                .team1(team1)
                .team2(team2)
                .scheduledDate(scheduledDate)
                .format(format != null ? format : "best_of_3_sets")
                .status("scheduled")
                .build();
        
        match = matchRepository.save(match);
        return mapper.toDto(match);
    }
    
    @Transactional
    public MatchResultDto submitResult(Integer matchId, String finalScore, Long submittedBy) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        String[] scores = finalScore.split("-");
        if (scores.length != 2) {
            throw new RuntimeException("Invalid score format. Use format: 2-0 or 2-1");
        }
        
        int team1Sets = Integer.parseInt(scores[0].trim());
        int team2Sets = Integer.parseInt(scores[1].trim());
        
        Team winnerTeam = team1Sets > team2Sets ? match.getTeam1() : match.getTeam2();
        Team loserTeam = team1Sets > team2Sets ? match.getTeam2() : match.getTeam1();
        
        int winnerPoints = 3;
        int loserPoints = 1;
        
        if (team1Sets == 2 && team2Sets == 0) {
            winnerPoints = 4;
        }
        
        MatchResult result = MatchResult.builder()
                .match(match)
                .winnerTeam(winnerTeam)
                .loserTeam(loserTeam)
                .finalScore(finalScore)
                .winnerPoints(winnerPoints)
                .loserPoints(loserPoints)
                .submittedBy(submittedBy)
                .disputed(false)
                .build();
        
        result = matchResultRepository.save(result);
        match.setStatus("completed");
        matchRepository.save(match);
        
        return mapper.toDto(result);
    }
    
    public List<MatchDto> getMatchesByTournament(Integer tournamentId) {
        return matchRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<MatchDto> getScheduledMatches(Integer tournamentId) {
        return matchRepository.findByTournamentIdAndStatus(tournamentId, "scheduled").stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    public MatchDto getMatch(Integer id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        return mapper.toDto(match);
    }
}
