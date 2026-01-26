package com.padelscore.http.controller;

import com.padelscore.dto.CreateTournamentRequest;
import com.padelscore.dto.TournamentDto;
import com.padelscore.dto.UpdateTournamentRequest;
import com.padelscore.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    
    private final TournamentService tournamentService;
    
    @GetMapping
    public ResponseEntity<List<TournamentDto>> getAllTournaments() {
        List<TournamentDto> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }
    
    @PostMapping
    public ResponseEntity<TournamentDto> createTournament(@Valid @RequestBody CreateTournamentRequest request) {
        TournamentDto tournament = tournamentService.createTournament(
                request.getTitle(),
                request.getDescription(),
                request.getCreatedBy(),
                request.getFormat(),
                request.getScoringSystem()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(tournament);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> getTournament(@PathVariable Integer id) {
        TournamentDto tournament = tournamentService.getTournament(id);
        return ResponseEntity.ok(tournament);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TournamentDto> updateTournament(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTournamentRequest request) {
        TournamentDto tournament = tournamentService.updateTournament(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getFormat(),
                request.getScoringSystem()
        );
        return ResponseEntity.ok(tournament);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Integer id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}
