package com.padelscore.http.controller;

import com.padelscore.dto.CreatePlayerRequest;
import com.padelscore.dto.PlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
import com.padelscore.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Игроки", description = "API для управления игроками")
public class PlayerController {
    
    private final PlayerService playerService;
    
    @GetMapping("/teams/{teamId}/players")
    @Operation(summary = "Получить игроков команды", description = "Возвращает список всех игроков указанной команды")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список игроков успешно получен",
                    content = @Content(schema = @Schema(implementation = PlayerDto.class)))
    })
    public ResponseEntity<List<PlayerDto>> getPlayersByTeam(
            @Parameter(description = "ID команды", required = true) @PathVariable Integer teamId) {
        List<PlayerDto> players = playerService.getPlayersByTeam(teamId);
        return ResponseEntity.ok(players);
    }
    
    @PostMapping("/players")
    @Operation(summary = "Создать игрока", description = "Создает нового игрока и добавляет его в команду")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Игрок успешно создан",
                    content = @Content(schema = @Schema(implementation = PlayerDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
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
    @Operation(summary = "Обновить игрока", description = "Обновляет информацию об игроке")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Игрок успешно обновлен",
                    content = @Content(schema = @Schema(implementation = PlayerDto.class))),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
        @ApiResponse(responseCode = "404", description = "Игрок не найден")
    })
    public ResponseEntity<PlayerDto> updatePlayer(
            @Parameter(description = "ID игрока", required = true) @PathVariable Integer id,
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
    @Operation(summary = "Удалить игрока", description = "Удаляет игрока из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Игрок успешно удален"),
        @ApiResponse(responseCode = "404", description = "Игрок не найден")
    })
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "ID игрока", required = true) @PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
