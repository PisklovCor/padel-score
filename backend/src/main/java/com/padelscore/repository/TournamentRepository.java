package com.padelscore.repository;

import com.padelscore.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

  List<Tournament> findByCreatedByPlayerProfileId(Integer playerProfileId);
}
