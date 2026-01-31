package com.padelscore.http.controller;

import com.padelscore.dto.CreatePlayerProfileRequest;
import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.UpdatePlayerRequest;
import com.padelscore.service.PlayerProfileService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/player-profiles")
@RequiredArgsConstructor
@Tag(name = "Профили игроков", description = "API для управления профилями игроков")
public class PlayerProfileController {

  private final PlayerProfileService playerProfileService;

  @GetMapping
  @Operation(summary = "Получить все профили игроков",
      description = "Возвращает список всех профилей игроков")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список профилей успешно получен",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class)))
  })
  public ResponseEntity<List<PlayerProfileDto>> getAllPlayerProfiles() {
    List<PlayerProfileDto> profiles = playerProfileService.getAllPlayerProfiles();
    return ResponseEntity.ok(profiles);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить профиль игрока", description = "Возвращает профиль игрока по ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Профиль игрока успешно получен",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class))),
      @ApiResponse(responseCode = "404", description = "Профиль игрока не найден")
  })
  public ResponseEntity<PlayerProfileDto> getPlayerProfile(
      @Parameter(description = "ID профиля игрока", required = true) @PathVariable Integer id) {
    PlayerProfileDto profile = playerProfileService.getPlayerProfile(id);
    return ResponseEntity.ok(profile);
  }

  @GetMapping("/telegram/{telegramId}")
  @Operation(summary = "Получить профиль игрока по Telegram ID",
      description = "Возвращает профиль игрока по Telegram ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Профиль игрока успешно получен",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class))),
      @ApiResponse(responseCode = "404", description = "Профиль игрока не найден")
  })
  public ResponseEntity<PlayerProfileDto> getPlayerProfileByTelegramId(
      @Parameter(description = "Telegram ID игрока", required = true)
      @PathVariable Long telegramId) {
    PlayerProfileDto profile = playerProfileService.getPlayerProfileByTelegramId(telegramId);
    return ResponseEntity.ok(profile);
  }

  @GetMapping("/search")
  @Operation(summary = "Поиск профилей игроков",
      description = "Поиск профилей игроков по имени или фамилии")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Результаты поиска получены",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class)))
  })
  public ResponseEntity<List<PlayerProfileDto>> searchPlayerProfiles(
      @Parameter(description = "Поисковый запрос", required = true) @RequestParam String q) {
    List<PlayerProfileDto> profiles = playerProfileService.searchPlayerProfiles(q);
    return ResponseEntity.ok(profiles);
  }

  @PostMapping
  @Operation(summary = "Создать профиль игрока", description = "Создает новый профиль игрока")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Профиль игрока успешно создан",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class))),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
  })
  public ResponseEntity<PlayerProfileDto> createPlayerProfile(
      @Valid @RequestBody CreatePlayerProfileRequest request) {
    PlayerProfileDto profile = playerProfileService.createPlayerProfile(
        request.getFirstName(),
        request.getLastName(),
        request.getNickname(),
        request.getTelegramId(),
        request.getRating()
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(profile);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить профиль игрока",
      description = "Обновляет профиль игрока (изменения применятся ко всем командам)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Профиль игрока успешно обновлен",
          content = @Content(schema = @Schema(implementation = PlayerProfileDto.class))),
      @ApiResponse(responseCode = "404", description = "Профиль игрока не найден")
  })
  public ResponseEntity<PlayerProfileDto> updatePlayerProfile(
      @Parameter(description = "ID профиля игрока", required = true) @PathVariable Integer id,
      @Valid @RequestBody UpdatePlayerRequest request) {
    PlayerProfileDto profile = playerProfileService.updatePlayerProfile(
        id,
        request.getFirstName(),
        request.getLastName(),
        request.getNickname(),
        request.getTelegramId(),
        request.getRating()
    );
    return ResponseEntity.ok(profile);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить профиль игрока",
      description = "Удаляет профиль игрока (только если игрок не состоит ни в одной команде)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Профиль игрока успешно удален"),
      @ApiResponse(responseCode = "400",
          description = "Невозможно удалить: игрок состоит в командах"),
      @ApiResponse(responseCode = "404", description = "Профиль игрока не найден")
  })
  public ResponseEntity<Void> deletePlayerProfile(
      @Parameter(description = "ID профиля игрока", required = true) @PathVariable Integer id) {
    playerProfileService.deletePlayerProfile(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/teams")
  @Operation(summary = "Получить команды игрока",
      description = "Возвращает список всех команд, в которых состоит игрок")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список команд успешно получен",
          content = @Content(schema = @Schema(implementation = TeamPlayerDto.class)))
  })
  public ResponseEntity<List<TeamPlayerDto>> getPlayerTeams(
      @Parameter(description = "ID профиля игрока", required = true) @PathVariable Integer id) {
    List<TeamPlayerDto> teams = playerProfileService.getPlayerTeams(id);
    return ResponseEntity.ok(teams);
  }
}
