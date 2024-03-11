package com.pjs.golf.warmup.service;

import com.pjs.golf.warmup.dto.WarmupRoundDto;
import com.pjs.golf.warmup.entity.WarmupRound;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarmupRoundMapper {
    WarmupRoundMapper Instance = Mappers.getMapper(WarmupRoundMapper.class);

    WarmupRound toEntity(WarmupRoundDto warmupRoundDto);
    WarmupRoundDto toDto(WarmupRound warmupRound);
}
