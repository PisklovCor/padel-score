package com.padelscore.http.controller;

import com.padelscore.dto.CreateMatchRequest;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.dto.SubmitResultRequest;
import com.padelscore.service.MatchService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Матчи", description = "API для управления матчами")
public class MatchController {

  private final MatchService matchService;

  @GetMapping("/tournaments/{tournamentId}/matches")
  @Operation(summary = "Получить матчи турнира",
      description = "Возвращает список всех матчей указанного турнира")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список матчей успешно получен",
          content = @Content(schema = @Schema(implementation = MatchDto.class)))
  })
  public ResponseEntity<List<MatchDto>> getMatchesByTournament(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer tournamentId) {
    List<MatchDto> matches = matchService.getMatchesByTournament(tournamentId);
    return ResponseEntity.ok(matches);
  }

  @PostMapping("/matches")
  @Operation(summary = "Создать матч", description = "Создает новый матч между двумя командами")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Матч успешно создан",
          content = @Content(schema = @Schema(implementation = MatchDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
  })
  public ResponseEntity<MatchDto> createMatch(@Valid @RequestBody CreateMatchRequest request) {
    MatchDto match = matchService.createMatch(
        request.getTournamentId(),
        request.getTeam1Id(),
        request.getTeam2Id(),
        request.getScheduledDate(),
        request.getFormat(),
        request.getLocation(),
        request.getCompleted()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(match);
  }

  @GetMapping("/matches/{id}")
  @Operation(summary = "Получить матч по ID", description = "Возвращает информацию о конкретном матче")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Матч найден",
          content = @Content(schema = @Schema(implementation = MatchDto.class))),
      @ApiResponse(responseCode = "404", description = "Матч не найден")
  })
  public ResponseEntity<MatchDto> getMatch(
      @Parameter(description = "ID матча", required = true) @PathVariable Integer id) {
    MatchDto match = matchService.getMatch(id);
    return ResponseEntity.ok(match);
  }

  @PutMapping("/matches/{id}/result")
  @Operation(summary = "Отправить результат матча",
      description = "Сохраняет результат матча, отправленный одним из участников")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Результат успешно сохранен",
          content = @Content(schema = @Schema(implementation = MatchResultDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные результата"),
      @ApiResponse(responseCode = "404", description = "Матч не найден")
  })
  public ResponseEntity<MatchResultDto> submitResult(
      @Parameter(description = "ID матча", required = true) @PathVariable Integer id,
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
  @Operation(summary = "Оспорить результат матча",
      description = "Помечает результат матча как оспоренный, требует повторной проверки")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Результат оспорен",
          content = @Content(schema = @Schema(implementation = MatchResultDto.class))),
      @ApiResponse(responseCode = "404", description = "Матч не найден")
  })
  public ResponseEntity<MatchResultDto> disputeResult(
      @Parameter(description = "ID матча", required = true) @PathVariable Integer id) {
    MatchResultDto result = matchService.disputeResult(id);
    return ResponseEntity.ok(result);
  }
}
