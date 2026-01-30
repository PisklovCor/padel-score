package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {

  private Integer id;

  private Integer tournamentId;

  private String name;

  private Long captainId;

  private String description;

  private String color;
}
