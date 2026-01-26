package com.padelscore.repository;

import com.padelscore.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentId(Integer tournamentId);
    List<Match> findByStatus(String status);
    List<Match> findByTournamentIdAndStatus(Integer tournamentId, String status);
}
