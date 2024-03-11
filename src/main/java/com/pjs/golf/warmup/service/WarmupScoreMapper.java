package com.pjs.golf.warmup.service;

import com.pjs.golf.warmup.dto.WarmupScoreDto;
import com.pjs.golf.warmup.entity.WarmupScore;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarmupScoreMapper {

    WarmupScoreMapper Instance = Mappers.getMapper(WarmupScoreMapper.class);

    WarmupScore toEntity(WarmupScoreDto warmupScoreDto);
    WarmupScoreDto toDto(WarmupScore warmupScore);
}
