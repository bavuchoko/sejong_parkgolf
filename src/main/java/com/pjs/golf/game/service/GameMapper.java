package com.pjs.golf.game.service;

import com.pjs.golf.game.dto.GameDto;
import com.pjs.golf.game.entity.Game;
import com.pjs.golf.warmup.dto.WarmupGameDto;
import com.pjs.golf.warmup.entity.WarmupGame;
import com.pjs.golf.warmup.service.WarmupGameMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameMapper Instance = Mappers.getMapper(GameMapper.class);
    Game toEntity(GameDto gameDto);
    GameDto toDto(Game game);
}
