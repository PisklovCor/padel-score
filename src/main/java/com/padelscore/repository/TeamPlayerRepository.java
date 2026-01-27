package com.padelscore.repository;

import com.padelscore.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Integer> {
    List<TeamPlayer> findByTeamId(Integer teamId);
    List<TeamPlayer> findByPlayerProfileId(Integer playerProfileId);
    Optional<TeamPlayer> findByTeamIdAndPlayerProfileId(Integer teamId, Integer playerProfileId);
    boolean existsByTeamIdAndPlayerProfileId(Integer teamId, Integer playerProfileId);
}
