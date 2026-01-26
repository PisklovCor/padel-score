package com.padelscore.http.controller;

import com.padelscore.dto.CreatePlayerRequest;
import com.padelscore.dto.PlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
import com.padelscore.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PlayerController {
    
    private final PlayerService playerService;
    
    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<List<PlayerDto>> getPlayersByTeam(@PathVariable Integer teamId) {
        List<PlayerDto> players = playerService.getPlayersByTeam(teamId);
        return ResponseEntity.ok(players);
    }
    
    @PostMapping("/players")
    public ResponseEntity<PlayerDto> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerDto player = playerService.createPlayer(
                request.getTeamId(),
                request.getFirstName(),
                request.getLastName(),
                request.getTelegramId(),
                request.getRating(),
                request.getPosition()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }
    
    @PutMapping("/players/{id}")
    public ResponseEntity<PlayerDto> updatePlayer(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePlayerRequest request) {
        PlayerDto player = playerService.updatePlayer(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getTelegramId(),
                request.getRating(),
                request.getPosition()
        );
        return ResponseEntity.ok(player);
    }
    
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
