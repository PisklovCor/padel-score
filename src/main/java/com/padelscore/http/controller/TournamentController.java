package com.padelscore.http.controller;

import com.padelscore.dto.CreateTournamentRequest;
import com.padelscore.dto.TournamentDto;
import com.padelscore.dto.UpdateTournamentRequest;
import com.padelscore.service.TournamentService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
@Tag(name = "Турниры", description = "API для управления турнирами")
public class TournamentController {

  private final TournamentService tournamentService;

  @GetMapping
  @Operation(summary = "Получить все турниры", description = "Возвращает список всех турниров в системе")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список турниров успешно получен",
          content = @Content(schema = @Schema(implementation = TournamentDto.class)))
  })
  public ResponseEntity<List<TournamentDto>> getAllTournaments() {
    List<TournamentDto> tournaments = tournamentService.getAllTournaments();
    return ResponseEntity.ok(tournaments);
  }

  @PostMapping
  @Operation(summary = "Создать турнир", description = "Создает новый турнир")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Турнир успешно создан",
          content = @Content(schema = @Schema(implementation = TournamentDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
  })
  public ResponseEntity<TournamentDto> createTournament(
      @Valid @RequestBody CreateTournamentRequest request) {
    TournamentDto tournament = tournamentService.createTournament(
        request.getTitle(),
        request.getDescription(),
        request.getCreatedBy(),
        request.getFormat(),
        request.getScoringSystem(),
        request.getPrize(),
        request.getStatus(),
        request.getCompleted()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(tournament);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить турнир по ID", description = "Возвращает информацию о конкретном турнире")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Турнир найден",
          content = @Content(schema = @Schema(implementation = TournamentDto.class))),
      @ApiResponse(responseCode = "404", description = "Турнир не найден")
  })
  public ResponseEntity<TournamentDto> getTournament(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer id) {
    TournamentDto tournament = tournamentService.getTournament(id);
    return ResponseEntity.ok(tournament);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить турнир", description = "Обновляет информацию о турнире")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Турнир успешно обновлен",
          content = @Content(schema = @Schema(implementation = TournamentDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
      @ApiResponse(responseCode = "404", description = "Турнир не найден")
  })
  public ResponseEntity<TournamentDto> updateTournament(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer id,
      @Valid @RequestBody UpdateTournamentRequest request) {
    TournamentDto tournament = tournamentService.updateTournament(
        id,
        request.getTitle(),
        request.getDescription(),
        request.getStartDate(),
        request.getEndDate(),
        request.getFormat(),
        request.getScoringSystem(),
        request.getPrize(),
        request.getStatus(),
        request.getCompleted()
    );
    return ResponseEntity.ok(tournament);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить турнир", description = "Удаляет турнир из системы")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Турнир успешно удален"),
      @ApiResponse(responseCode = "404", description = "Турнир не найден")
  })
  public ResponseEntity<Void> deleteTournament(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer id) {
    tournamentService.deleteTournament(id);
    return ResponseEntity.noContent().build();
  }
}
