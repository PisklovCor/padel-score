package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDto {

  private Integer matchId;

  private Integer winnerTeamId;

  private Integer loserTeamId;

  private String winnerTeamName;

  private String loserTeamName;

  private String finalScore;

  private Integer winnerPoints;

  private Integer loserPoints;
}
