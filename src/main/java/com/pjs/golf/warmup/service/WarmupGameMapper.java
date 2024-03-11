package com.pjs.golf.warmup.service;


import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.service.AccountMapper;
import com.pjs.golf.warmup.dto.WarmupGameDto;
import com.pjs.golf.warmup.entity.WarmupGame;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AccountMapper.class)
public interface WarmupGameMapper {


    WarmupGameMapper Instance = Mappers.getMapper(WarmupGameMapper.class);
    @Mapping(source = "players", target = "players", qualifiedByName = "entiTyToEntity")
    WarmupGame toEntity(WarmupGameDto warmupGameDto);

    @Mapping(source = "players", target = "players", qualifiedByName = "entiTyToEntity")
    WarmupGameDto toDto(WarmupGame warmupGame);
    @Mapping(source = "players", target = "players", qualifiedByName = "withoutRoles")
    WarmupGameDto toDtoWithoutRoles(WarmupGame warmupGame);

    @Mapping(source = "rounds", target = "rounds", ignore = true)
    @Mapping(source = "players", target = "players", qualifiedByName = "withoutRoles")
    WarmupGameDto toDtoForResource(WarmupGame warmupGame);

}
