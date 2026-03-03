package com.padelscore.service;

import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.entity.Match;
import com.padelscore.entity.MatchResult;
import com.padelscore.entity.PlayerProfile;
import com.padelscore.entity.Team;
import com.padelscore.repository.MatchRepository;
import com.padelscore.repository.MatchResultRepository;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import static com.padelscore.entity.enums.MatchStatus.COMPLETED;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final TeamRepository teamRepository;

  private final MatchRepository matchRepository;

  private final MatchResultRepository matchResultRepository;

  private final PlayerProfileRepository playerProfileRepository;

  private final EntityMapper entityMapper;

  public List<LeaderboardEntryDto> getLeaderboard(Integer tournamentId) {
    List<Team> teams = teamRepository.findByTournamentId(tournamentId);
    List<Match> completedMatches = matchRepository.findByTournamentIdAndStatus(tournamentId,
        COMPLETED);
    Map<Integer, LeaderboardEntryDto> leaderboard = new ConcurrentHashMap<>();
    initLeaderboard(teams, leaderboard);
    for (Match match : completedMatches) {
      matchResultRepository.findByMatchId(match.getId())
          .ifPresent(result -> applyMatchResult(leaderboard, match, result));
    }
    updateWinRates(leaderboard);
    return sortLeaderboard(leaderboard);
  }

  private void initLeaderboard(List<Team> teams, Map<Integer, LeaderboardEntryDto> leaderboard) {
    for (Team team : teams) {
      LeaderboardEntryDto entry = LeaderboardEntryDto.builder()
          .teamId(team.getId())
          .teamName(team.getName())
          .matches(0)
          .wins(0)
          .losses(0)
          .setsWon(0)
          .setsLost(0)
          .gamesWon(0)
          .gamesLost(0)
          .points(0)
          .winRate(0.0)
          .build();
      leaderboard.put(team.getId(), entry);
    }
  }

  private void applyMatchResult(Map<Integer, LeaderboardEntryDto> leaderboard, Match match,
      MatchResult result) {
    LeaderboardEntryDto winner = leaderboard.get(result.getWinnerTeam().getId());
    LeaderboardEntryDto loser = leaderboard.get(result.getLoserTeam().getId());
    if (winner == null || loser == null) {
      return;
    }
    updateWinLossStats(winner, loser, result);
    updateSetsStats(match, result, winner, loser);
  }

  private void updateWinLossStats(LeaderboardEntryDto winner, LeaderboardEntryDto loser,
      MatchResult result) {
    winner.setMatches(winner.getMatches() + 1);
    winner.setWins(winner.getWins() + 1);
    winner.setPoints(winner.getPoints() + result.getWinnerPoints());
    loser.setMatches(loser.getMatches() + 1);
    loser.setLosses(loser.getLosses() + 1);
    loser.setPoints(loser.getPoints() + result.getLoserPoints());
  }

  private void updateSetsStats(Match match, MatchResult result, LeaderboardEntryDto winner,
      LeaderboardEntryDto loser) {
    String[] scores = result.getFinalScore().split("-");
    if (scores.length != 2) {
      return;
    }
    int winnerSets = Integer.parseInt(scores[0].trim());
    int loserSets = Integer.parseInt(scores[1].trim());
    boolean team1Won = result.getWinnerTeam().getId().equals(match.getTeam1().getId());
    if (team1Won) {
      winner.setSetsWon(winner.getSetsWon() + winnerSets);
      winner.setSetsLost(winner.getSetsLost() + loserSets);
      loser.setSetsWon(loser.getSetsWon() + loserSets);
      loser.setSetsLost(loser.getSetsLost() + winnerSets);
    } else {
      winner.setSetsWon(winner.getSetsWon() + loserSets);
      winner.setSetsLost(winner.getSetsLost() + winnerSets);
      loser.setSetsWon(loser.getSetsWon() + winnerSets);
      loser.setSetsLost(loser.getSetsLost() + loserSets);
    }
  }

  private void updateWinRates(Map<Integer, LeaderboardEntryDto> leaderboard) {
    for (LeaderboardEntryDto entry : leaderboard.values()) {
      if (entry.getMatches() > 0) {
        entry.setWinRate((double) entry.getWins() / entry.getMatches());
      }
    }
  }

  private List<LeaderboardEntryDto> sortLeaderboard(Map<Integer, LeaderboardEntryDto> leaderboard) {
    Comparator<LeaderboardEntryDto> comparator = Comparator
        .comparingInt(LeaderboardEntryDto::getPoints).reversed()
        .thenComparing(e -> e.getSetsWon() - e.getSetsLost(), Comparator.reverseOrder())
        .thenComparing(e -> e.getGamesWon() - e.getGamesLost(), Comparator.reverseOrder());
    return leaderboard.values().stream()
        .sorted(comparator)
        .collect(Collectors.toList());
  }

  public Map<String, Object> getTournamentStats(Integer tournamentId) {
    List<LeaderboardEntryDto> leaderboard = getLeaderboard(tournamentId);

    int totalMatches = leaderboard.stream()
        .mapToInt(LeaderboardEntryDto::getMatches)
        .sum();

    int totalTeams = leaderboard.size();

    return Map.of(
        "tournamentId", tournamentId,
        "totalTeams", totalTeams,
        "totalMatches", totalMatches,
        "leaderboard", leaderboard
    );
  }

  /**
   * Топ игроков по личному рейтингу (для главного меню и других экранов).
   *
   * @param limit максимальное количество игроков (например, 10)
   * @return список DTO, отсортированный по убыванию рейтинга
   */
  public List<PlayerProfileDto> getTopPlayersByRating(int limit) {
    List<PlayerProfile> top = playerProfileRepository.findByRatingIsNotNullOrderByRatingDesc(
        PageRequest.of(0, limit));
    return top.stream()
        .map(entityMapper::toDto)
        .collect(Collectors.toList());
  }
}
