package com.padelscore.repository;

import com.padelscore.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByTournamentId(Integer tournamentId);
    List<UserRole> findByUserId(Long userId);
    Optional<UserRole> findByTournamentIdAndUserId(Integer tournamentId, Long userId);
}
