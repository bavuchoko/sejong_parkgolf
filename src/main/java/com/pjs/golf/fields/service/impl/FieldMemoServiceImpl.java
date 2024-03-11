package com.pjs.golf.fields.service.impl;

import com.pjs.golf.fields.dto.FieldsDto;
import com.pjs.golf.fields.service.FieldMemoService;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldMemoServiceImpl implements FieldMemoService {
    @Override
    public List getFiledMemos(int fieldId, Integer id) {
        return null;
    }

    @Override
    public EntityModel getResource(List memos) {
        return null;
    }

    @Override
    public void createMemo(FieldsDto fieldsDto) {

    }

    @Override
    public void updateMemo(FieldsDto fieldsDto) {

    }
}
