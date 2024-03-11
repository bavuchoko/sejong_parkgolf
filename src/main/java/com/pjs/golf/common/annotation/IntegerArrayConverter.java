package com.pjs.golf.common.annotation;

import com.pjs.golf.common.exception.ConvertFailedException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Converter
public class IntegerArrayConverter implements AttributeConverter<List<Integer>, String> {

    private final String SPLIT_CHAR = ",";
    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return attribute.stream().map(String::valueOf).collect(Collectors.joining(SPLIT_CHAR));
        } catch (Exception e) {
            throw new ConvertFailedException("[value to DB] IntegerArrayConverter Failed");
        }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            return Arrays.stream(dbData.split(SPLIT_CHAR)).map(Integer::parseInt).collect(Collectors.toList());
        }catch (Exception e) {
            throw new ConvertFailedException("[DB to value] StringArrayConverter Failed");
        }
    }
}
