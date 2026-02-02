package com.padelscore.util;

import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.dto.TeamDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.TournamentDto;
import com.padelscore.entity.Match;
import com.padelscore.entity.MatchResult;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.entity.Team;
import com.padelscore.entity.TeamPlayer;
import com.padelscore.entity.Tournament;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

  public TournamentDto toDto(Tournament tournament) {
    return TournamentDto.builder()
        .id(tournament.getId())
        .title(tournament.getTitle())
        .description(tournament.getDescription())
        .createdBy(tournament.getCreatedBy())
        .startDate(tournament.getStartDate())
        .endDate(tournament.getEndDate())
        .format(tournament.getFormat())
        .scoringSystem(tournament.getScoringSystem())
        .prize(tournament.getPrize())
        .status(tournament.getStatus() != null ? tournament.getStatus().name() : null)
        .completed(tournament.getCompleted())
        .build();
  }

  public TeamDto toDto(Team team) {
    return TeamDto.builder()
        .id(team.getId())
        .tournamentId(team.getTournament().getId())
        .name(team.getName())
        .captainId(team.getCaptainId())
        .description(team.getDescription())
        .color(team.getColor())
        .build();
  }

  public TeamPlayerDto toDto(TeamPlayer teamPlayer) {
    PlayerProfile profile = teamPlayer.getPlayerProfile();
    return TeamPlayerDto.builder()
        .id(teamPlayer.getId())
        .teamId(teamPlayer.getTeam().getId())
        .position(teamPlayer.getPosition() != null ? teamPlayer.getPosition().name() : null)
        .playerProfileId(profile.getId())
        .firstName(profile.getFirstName())
        .lastName(profile.getLastName())
        .nickname(profile.getNickname())
        .telegramId(profile.getTelegramId())
        .rating(profile.getRating())
        .build();
  }

  public PlayerProfileDto toDto(PlayerProfile profile) {
    return PlayerProfileDto.builder()
        .id(profile.getId())
        .firstName(profile.getFirstName())
        .lastName(profile.getLastName())
        .nickname(profile.getNickname())
        .telegramId(profile.getTelegramId())
        .rating(profile.getRating())
        .build();
  }

  public MatchDto toDto(Match match) {
    return MatchDto.builder()
        .id(match.getId())
        .tournamentId(match.getTournament().getId())
        .team1Id(match.getTeam1().getId())
        .team2Id(match.getTeam2().getId())
        .team1Name(match.getTeam1().getName())
        .team2Name(match.getTeam2().getName())
        .scheduledDate(match.getScheduledDate())
        .status(match.getStatus() != null ? match.getStatus().name() : null)
        .format(match.getFormat())
        .location(match.getLocation())
        .completed(match.getCompleted())
        .build();
  }

  public MatchResultDto toDto(MatchResult result) {
    return MatchResultDto.builder()
        .matchId(result.getMatch().getId())
        .winnerTeamId(result.getWinnerTeam().getId())
        .loserTeamId(result.getLoserTeam().getId())
        .winnerTeamName(result.getWinnerTeam().getName())
        .loserTeamName(result.getLoserTeam().getName())
        .finalScore(result.getFinalScore())
        .winnerPoints(result.getWinnerPoints())
        .loserPoints(result.getLoserPoints())
        .build();
  }
}
