package com.padelscore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {
    private Integer id;
    private Integer tournamentId;
    private Integer team1Id;
    private Integer team2Id;
    private String team1Name;
    private String team2Name;
    private LocalDateTime scheduledDate;
    private String status;
    private String format;
}
