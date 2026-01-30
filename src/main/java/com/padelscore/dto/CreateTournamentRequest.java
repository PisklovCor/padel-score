package com.padelscore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTournamentRequest {

  @NotBlank(message = "Название турнира обязательно")
  private String title;

  private String description;

  @NotNull(message = "ID создателя обязателен")
  private Long createdBy;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private String format; // 'group', 'knockout', 'mixed'

  private String scoringSystem; // 'points', 'win_loss'

  private String prize;

  private String status;

  private Boolean completed;
}
