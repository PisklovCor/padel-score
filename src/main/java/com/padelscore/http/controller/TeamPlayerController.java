package com.padelscore.http.controller;

import com.padelscore.dto.CreatePlayerRequest;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
import com.padelscore.service.TeamPlayerService;
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
@RequestMapping("/api/v1/team-players")
@RequiredArgsConstructor
@Tag(name = "Связи игрок-команда",
    description = "API для управления связями игроков с командами")
public class TeamPlayerController {

  private final TeamPlayerService teamPlayerService;

  @GetMapping("/team/{teamId}")
  @Operation(summary = "Получить игроков команды",
      description = "Возвращает список всех игроков указанной команды")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список игроков успешно получен",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class)))
  })
  public ResponseEntity<List<TeamPlayerDto>> getPlayersByTeam(
      @Parameter(description = "ID команды", required = true) @PathVariable Integer teamId) {
    List<TeamPlayerDto> players = teamPlayerService.getPlayersByTeam(teamId);
    return ResponseEntity.ok(players);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить связь игрок-команда",
      description = "Возвращает информацию о связи игрока с командой")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Связь успешно получена",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class))),
      @ApiResponse(responseCode = "404", description = "Связь не найдена")
  })
  public ResponseEntity<TeamPlayerDto> getTeamPlayer(
      @Parameter(description = "ID связи (TeamPlayer)", required = true) @PathVariable Integer id) {
    TeamPlayerDto teamPlayer = teamPlayerService.getTeamPlayer(id);
    return ResponseEntity.ok(teamPlayer);
  }

  @PostMapping
  @Operation(summary = "Добавить игрока в команду",
      description = "Создает связь между существующим профилем игрока и командой")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Игрок успешно добавлен в команду",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class))),
      @ApiResponse(responseCode = "400", description = "Игрок уже состоит в этой команде")
  })
  public ResponseEntity<TeamPlayerDto> addPlayerToTeam(
      @RequestBody AddPlayerToTeamRequest request) {
    TeamPlayerDto teamPlayer = teamPlayerService.addPlayerToTeam(
        request.getTeamId(),
        request.getPlayerProfileId(),
        request.getPosition()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(teamPlayer);
  }

  @PostMapping("/create")
  @Operation(summary = "Создать игрока и добавить в команду",
      description = "Создает нового игрока (или использует существующий профиль) и добавляет его в команду")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Игрок успешно создан и добавлен в команду",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
  })
  public ResponseEntity<TeamPlayerDto> createPlayer(
      @Valid @RequestBody CreatePlayerRequest request) {
    TeamPlayerDto player = teamPlayerService.createPlayer(
        request.getTeamId(),
        request.getFirstName(),
        request.getLastName(),
        request.getTelegramId(),
        request.getRating(),
        request.getPosition()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(player);
  }

  @PutMapping("/{id}/position")
  @Operation(summary = "Обновить позицию игрока в команде",
      description = "Обновляет только позицию игрока в конкретной команде")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Позиция успешно обновлена",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class))),
      @ApiResponse(responseCode = "404", description = "Связь не найдена")
  })
  public ResponseEntity<TeamPlayerDto> updateTeamPlayerPosition(
      @Parameter(description = "ID связи (TeamPlayer)", required = true) @PathVariable Integer id,
      @RequestBody UpdateTeamPlayerRequest request) {
    TeamPlayerDto teamPlayer = teamPlayerService.updateTeamPlayer(id, request.getPosition());
    return ResponseEntity.ok(teamPlayer);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить игрока в команде",
      description = "Обновляет информацию об игроке (профиль и позицию в команде)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Игрок успешно обновлен",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
      @ApiResponse(responseCode = "404", description = "Игрок не найден")
  })
  public ResponseEntity<TeamPlayerDto> updatePlayer(
      @Parameter(description = "ID связи игрока с командой (TeamPlayer)",
          required = true) @PathVariable Integer id,
      @Valid @RequestBody UpdatePlayerRequest request) {
    TeamPlayerDto player = teamPlayerService.updatePlayer(
        id,
        request.getFirstName(),
        request.getLastName(),
        request.getTelegramId(),
        request.getRating(),
        request.getPosition()
    );
    return ResponseEntity.ok(player);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить связь игрок-команда",
      description = "Удаляет связь игрока с командой (профиль игрока сохраняется)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Связь успешно удалена"),
      @ApiResponse(responseCode = "404", description = "Связь не найдена")
  })
  public ResponseEntity<Void> deleteTeamPlayer(
      @Parameter(description = "ID связи (TeamPlayer)", required = true) @PathVariable Integer id) {
    teamPlayerService.deleteTeamPlayer(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/team/{teamId}/player/{playerProfileId}")
  @Operation(summary = "Удалить игрока из команды",
      description = "Удаляет связь игрока с конкретной командой")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Игрок успешно удален из команды"),
      @ApiResponse(responseCode = "404", description = "Игрок не найден в команде")
  })
  public ResponseEntity<Void> removePlayerFromTeam(
      @Parameter(description = "ID команды", required = true) @PathVariable Integer teamId,
      @Parameter(description = "ID профиля игрока", required = true)
      @PathVariable Integer playerProfileId) {
    teamPlayerService.removePlayerFromTeam(teamId, playerProfileId);
    return ResponseEntity.noContent().build();
  }

  // Внутренние классы для запросов
  @lombok.Data
  @lombok.NoArgsConstructor
  @lombok.AllArgsConstructor
  public static class AddPlayerToTeamRequest {

    private Integer teamId;

    private Integer playerProfileId;

    private String position;
  }

  @lombok.Data
  @lombok.NoArgsConstructor
  @lombok.AllArgsConstructor
  public static class UpdateTeamPlayerRequest {

    private String position;
  }

}
