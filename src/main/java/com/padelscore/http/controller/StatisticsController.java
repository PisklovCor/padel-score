package com.padelscore.http.controller;

import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Статистика", description = "API для получения статистики и рейтингов")
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping("/tournaments/{tournamentId}/leaderboard")
  @Operation(summary = "Получить таблицу лидеров турнира",
      description = "Возвращает рейтинговую таблицу команд турнира")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Таблица лидеров успешно получена",
          content = @Content(schema = @Schema(implementation = LeaderboardEntryDto.class)))
  })
  public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer tournamentId) {
    List<LeaderboardEntryDto> leaderboard = statisticsService.getLeaderboard(tournamentId);
    return ResponseEntity.ok(leaderboard);
  }

  @GetMapping("/tournaments/{tournamentId}/stats")
  @Operation(summary = "Получить статистику турнира",
      description = "Возвращает общую статистику турнира включая количество команд, матчей и таблицу лидеров")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Статистика турнира успешно получена")
  })
  public ResponseEntity<Map<String, Object>> getTournamentStats(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer tournamentId) {
    Map<String, Object> stats = statisticsService.getTournamentStats(tournamentId);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/players/{playerId}/stats")
  @Operation(summary = "Получить статистику игрока",
      description = "Возвращает статистику конкретного игрока (в разработке)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Статистика игрока получена")
  })
  public ResponseEntity<Map<String, Object>> getPlayerStats(
      @Parameter(description = "ID игрока", required = true) @PathVariable Integer playerId) {
    // TODO: Реализовать статистику игрока, когда будет готов сервис с методами статистики
    Map<String, Object> stats = Map.of(
        "playerId", playerId,
        "message", "Player statistics not implemented yet"
    );
    return ResponseEntity.ok(stats);
  }
}
