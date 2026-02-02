package com.padelscore.repository;

import com.padelscore.entity.Match;
import com.padelscore.entity.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

  List<Match> findByTournamentId(Integer tournamentId);

  List<Match> findByStatus(MatchStatus status);

  List<Match> findByTournamentIdAndStatus(Integer tournamentId, MatchStatus status);
}
