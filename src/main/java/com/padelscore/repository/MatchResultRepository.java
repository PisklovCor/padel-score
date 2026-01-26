package com.padelscore.repository;

import com.padelscore.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Integer> {
    Optional<MatchResult> findByMatchId(Integer matchId);
    List<MatchResult> findByWinnerTeamIdOrLoserTeamId(Integer winnerTeamId, Integer loserTeamId);
}
