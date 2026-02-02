package com.padelscore.repository;

import com.padelscore.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

  List<Team> findByTournamentId(Integer tournamentId);

  List<Team> findByCaptainPlayerProfileId(Integer playerProfileId);
}
