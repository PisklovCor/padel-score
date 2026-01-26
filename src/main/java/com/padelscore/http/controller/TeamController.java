package com.padelscore.http.controller;

import com.padelscore.dto.CreateTeamRequest;
import com.padelscore.dto.TeamDto;
import com.padelscore.dto.UpdateTeamRequest;
import com.padelscore.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TeamController {
    
    private final TeamService teamService;
    
    @GetMapping("/tournaments/{tournamentId}/teams")
    public ResponseEntity<List<TeamDto>> getTeamsByTournament(@PathVariable Integer tournamentId) {
        List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);
        return ResponseEntity.ok(teams);
    }
    
    @PostMapping("/teams")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        TeamDto team = teamService.createTeam(
                request.getTournamentId(),
                request.getName(),
                request.getCaptainId(),
                request.getDescription(),
                request.getColor()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }
    
    @PutMapping("/teams/{id}")
    public ResponseEntity<TeamDto> updateTeam(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTeamRequest request) {
        TeamDto team = teamService.updateTeam(
                id,
                request.getName(),
                request.getCaptainId(),
                request.getDescription(),
                request.getColor()
        );
        return ResponseEntity.ok(team);
    }
    
    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Integer id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
