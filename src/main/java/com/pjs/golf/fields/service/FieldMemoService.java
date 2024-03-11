package com.pjs.golf.fields.service;

import com.pjs.golf.fields.dto.FieldsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;


public interface FieldMemoService {
    List getFiledMemos(int fieldId, Integer id);

    EntityModel getResource(List memos);

    void createMemo(FieldsDto fieldsDto);

    void updateMemo(FieldsDto fieldsDto);
}
