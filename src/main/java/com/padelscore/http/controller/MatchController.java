package com.padelscore.http.controller;

import com.padelscore.dto.CreateMatchRequest;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.dto.SubmitResultRequest;
import com.padelscore.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MatchController {
    
    private final MatchService matchService;
    
    @GetMapping("/tournaments/{tournamentId}/matches")
    public ResponseEntity<List<MatchDto>> getMatchesByTournament(@PathVariable Integer tournamentId) {
        List<MatchDto> matches = matchService.getMatchesByTournament(tournamentId);
        return ResponseEntity.ok(matches);
    }
    
    @PostMapping("/matches")
    public ResponseEntity<MatchDto> createMatch(@Valid @RequestBody CreateMatchRequest request) {
        MatchDto match = matchService.createMatch(
                request.getTournamentId(),
                request.getTeam1Id(),
                request.getTeam2Id(),
                request.getScheduledDate(),
                request.getFormat()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }
    
    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchDto> getMatch(@PathVariable Integer id) {
        MatchDto match = matchService.getMatch(id);
        return ResponseEntity.ok(match);
    }
    
    @PutMapping("/matches/{id}/result")
    public ResponseEntity<MatchResultDto> submitResult(
            @PathVariable Integer id,
            @Valid @RequestBody SubmitResultRequest request) {
        MatchResultDto result = matchService.submitResult(
                id,
                request.getFinalScore(),
                request.getSubmittedBy(),
                request.getNotes()
        );
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/matches/{id}/dispute")
    public ResponseEntity<MatchResultDto> disputeResult(@PathVariable Integer id) {
        MatchResultDto result = matchService.disputeResult(id);
        return ResponseEntity.ok(result);
    }
}
