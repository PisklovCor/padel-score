package com.padelscore.service;

import com.padelscore.dto.PlayerDto;
import com.padelscore.entity.Player;
import com.padelscore.entity.Team;
import com.padelscore.repository.PlayerRepository;
import com.padelscore.repository.TeamRepository;
import com.padelscore.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final EntityMapper mapper;
    
    @Transactional
    public PlayerDto createPlayer(Integer teamId, String firstName, String lastName, 
                                 Long telegramId, Integer rating, String position) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        Player player = Player.builder()
                .team(team)
                .firstName(firstName)
                .lastName(lastName)
                .telegramId(telegramId)
                .rating(rating)
                .position(position != null ? position : "primary")
                .build();
        
        player = playerRepository.save(player);
        return mapper.toDto(player);
    }
    
    public List<PlayerDto> getPlayersByTeam(Integer teamId) {
        return playerRepository.findByTeamId(teamId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    
    public PlayerDto getPlayer(Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        return mapper.toDto(player);
    }
    
    @Transactional
    public PlayerDto updatePlayer(Integer id, String firstName, String lastName,
                                  Long telegramId, Integer rating, String position) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (firstName != null) {
            player.setFirstName(firstName);
        }
        if (lastName != null) {
            player.setLastName(lastName);
        }
        if (telegramId != null) {
            player.setTelegramId(telegramId);
        }
        if (rating != null) {
            player.setRating(rating);
        }
        if (position != null) {
            player.setPosition(position);
        }
        
        player = playerRepository.save(player);
        return mapper.toDto(player);
    }
    
    @Transactional
    public void deletePlayer(Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        playerRepository.delete(player);
    }
}
