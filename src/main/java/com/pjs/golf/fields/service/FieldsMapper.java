package com.pjs.golf.fields.service;

import com.pjs.golf.fields.dto.FieldsDto;
import com.pjs.golf.fields.entity.Fields;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FieldsMapper {
    FieldsMapper Instance = Mappers.getMapper(FieldsMapper.class);
    Fields toEntity(FieldsDto fieldsDto);
    FieldsDto toDto(Fields fields);
}
