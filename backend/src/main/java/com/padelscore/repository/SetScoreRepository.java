package com.padelscore.repository;

import com.padelscore.entity.SetScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetScoreRepository extends JpaRepository<SetScore, Integer> {

  List<SetScore> findByMatchId(Integer matchId);
}
