package com.padelscore.service;

import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.entity.Match;
import com.padelscore.entity.MatchResult;
import com.padelscore.entity.Team;
import com.padelscore.entity.Tournament;
import com.padelscore.entity.enums.MatchStatus;
import com.padelscore.repository.MatchRepository;
import com.padelscore.repository.MatchResultRepository;
import com.padelscore.repository.PlayerProfileRepository;
import com.padelscore.repository.TeamRepository;
import com.padelscore.repository.TournamentRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;

  private final MatchResultRepository matchResultRepository;

  private final TournamentRepository tournamentRepository;

  private final TeamRepository teamRepository;

  private final PlayerProfileRepository playerProfileRepository;

  private final TournamentService tournamentService;

  private final EntityMapper mapper;

  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public MatchDto createMatch(Integer tournamentId, Integer team1Id, Integer team2Id,
      LocalDateTime scheduledDate, String format, String location,
      Boolean completed) {
    Tournament tournament = tournamentRepository.findById(tournamentId)
        .orElseThrow(() -> new RuntimeException("Tournament not found"));
    Team team1 = teamRepository.findById(team1Id)
        .orElseThrow(() -> new RuntimeException("Team 1 not found"));
    Team team2 = teamRepository.findById(team2Id)
        .orElseThrow(() -> new RuntimeException("Team 2 not found"));

    if (team1Id.equals(team2Id)) {
      throw new RuntimeException("Team 1 and Team 2 cannot be the same");
    }

    Match match = Match.builder()
        .tournament(tournament)
        .team1(team1)
        .team2(team2)
        .scheduledDate(scheduledDate)
        .format(format != null ? format : "best_of_3_sets")
        .location(location)
        .status(MatchStatus.SCHEDULED)
        .completed(completed != null ? completed : false)
        .build();

    match = matchRepository.save(match);
    return mapper.toDto(match);
  }

  @Transactional
  public MatchResultDto submitResult(Integer matchId, String finalScore,
      Integer submittedByPlayerProfileId, String notes) {
    jdbcTemplate.update("SET LOCAL app.player_profile_id = " + submittedByPlayerProfileId);
    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Match not found"));

    playerProfileRepository.findById(submittedByPlayerProfileId)
        .orElseThrow(() -> new RuntimeException("Player profile not found"));

    // Проверка прав: только создатель турнира может редактировать результаты
    if (!tournamentService.isTournamentCreator(submittedByPlayerProfileId,
        match.getTournament().getId())) {
      throw new RuntimeException("Только создатель турнира может редактировать результаты матчей");
    }

    ScoreCalculationResult calculation = calculateScore(match, finalScore);

    MatchResult result = matchResultRepository.findByMatchId(matchId).orElse(null);

    if (result == null) {
      // Создаем новый результат
      result = MatchResult.builder()
          .match(match)
          .winnerTeam(calculation.winnerTeam())
          .loserTeam(calculation.loserTeam())
          .finalScore(finalScore)
          .winnerPoints(calculation.winnerPoints())
          .loserPoints(calculation.loserPoints())
          .submittedByPlayerProfileId(submittedByPlayerProfileId)
          .notes(notes)
          .disputed(false)
          .build();
    } else {
      // Обновляем существующий результат
      result.setWinnerTeam(calculation.winnerTeam());
      result.setLoserTeam(calculation.loserTeam());
      result.setFinalScore(finalScore);
      result.setWinnerPoints(calculation.winnerPoints());
      result.setLoserPoints(calculation.loserPoints());
      result.setSubmittedByPlayerProfileId(submittedByPlayerProfileId);
      if (notes != null) {
        result.setNotes(notes);
      }
      result.setDisputed(false);
    }

    result = matchResultRepository.save(result);
    match.setStatus(MatchStatus.COMPLETED);
    match.setCompleted(true);
    matchRepository.save(match);

    return mapper.toDto(result);
  }

  private ScoreCalculationResult calculateScore(Match match, String finalScore) {
    String[] scores = finalScore.split("-");
    if (scores.length != 2) {
      throw new RuntimeException("Invalid score format. Use format: 2-0, 2-1, 0-2 or 1-2");
    }

    int team1Sets = Integer.parseInt(scores[0].trim());
    int team2Sets = Integer.parseInt(scores[1].trim());

    Team winnerTeam = team1Sets > team2Sets ? match.getTeam1() : match.getTeam2();
    Team loserTeam = team1Sets > team2Sets ? match.getTeam2() : match.getTeam1();

    int winnerPoints = 3;
    int loserPoints = 1;

    if ((team1Sets == 2 && team2Sets == 0) || (team1Sets == 0 && team2Sets == 2)) {
      winnerPoints = 4;
    }

    return new ScoreCalculationResult(winnerTeam, loserTeam, winnerPoints, loserPoints);
  }

  private record ScoreCalculationResult(Team winnerTeam, Team loserTeam, int winnerPoints,
                                        int loserPoints) {

  }

  @Transactional(readOnly = true)
  public List<MatchDto> getMatchesByTournament(Integer tournamentId) {
    return matchRepository.findByTournamentId(tournamentId).stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public MatchDto getMatch(Integer id) {
    Match match = matchRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Match not found"));
    return mapper.toDto(match);
  }

  @Transactional(readOnly = true)
  public MatchResultDto getMatchResult(Integer matchId) {
    MatchResult result = matchResultRepository.findByMatchId(matchId)
        .orElseThrow(() -> new RuntimeException("Match result not found"));
    return mapper.toDto(result);
  }

  @Transactional
  public MatchResultDto disputeResult(Integer matchId) {
    // Проверяем существование матча
    matchRepository.findById(matchId)
        .orElseThrow(() -> new RuntimeException("Match not found"));

    MatchResult result = matchResultRepository.findByMatchId(matchId)
        .orElseThrow(() -> new RuntimeException("Match result not found"));

    result.setDisputed(true);
    result = matchResultRepository.save(result);
    return mapper.toDto(result);
  }
}
