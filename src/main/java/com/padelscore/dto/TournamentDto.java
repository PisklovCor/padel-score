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
public class TournamentDto {
    private Integer id;
    private String title;
    private String description;
    private Long createdBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format;
    private String scoringSystem;
}
