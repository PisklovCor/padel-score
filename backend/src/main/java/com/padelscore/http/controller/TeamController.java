package com.padelscore.http.controller;

import com.padelscore.dto.CreateTeamRequest;
import com.padelscore.dto.TeamDto;
import com.padelscore.dto.UpdateTeamRequest;
import com.padelscore.service.TeamService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Команды", description = "API для управления командами")
public class TeamController {

  private final TeamService teamService;

  @GetMapping("/tournaments/{tournamentId}/teams")
  @Operation(summary = "Получить команды турнира", description = "Возвращает список всех команд указанного турнира")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список команд успешно получен",
          content = @Content(schema = @Schema(implementation = TeamDto.class)))
  })
  public ResponseEntity<List<TeamDto>> getTeamsByTournament(
      @Parameter(description = "ID турнира", required = true) @PathVariable Integer tournamentId) {
    List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);
    return ResponseEntity.ok(teams);
  }

  @PostMapping("/teams")
  @Operation(summary = "Создать команду", description = "Создает новую команду и добавляет ее в турнир")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Команда успешно создана",
          content = @Content(schema = @Schema(implementation = TeamDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
  })
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
  @Operation(summary = "Обновить команду", description = "Обновляет информацию о команде")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Команда успешно обновлена",
          content = @Content(schema = @Schema(implementation = TeamDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
      @ApiResponse(responseCode = "404", description = "Команда не найдена")
  })
  public ResponseEntity<TeamDto> updateTeam(
      @Parameter(description = "ID команды", required = true) @PathVariable Integer id,
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
  @Operation(summary = "Удалить команду", description = "Удаляет команду из системы")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Команда успешно удалена"),
      @ApiResponse(responseCode = "404", description = "Команда не найдена")
  })
  public ResponseEntity<Void> deleteTeam(
      @Parameter(description = "ID команды", required = true) @PathVariable Integer id) {
    teamService.deleteTeam(id);
    return ResponseEntity.noContent().build();
  }
}
