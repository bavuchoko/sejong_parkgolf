package com.pjs.golf.fields.service.impl;

import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.exception.NoSuchDataCustomException;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.fields.repository.FieldsJpaRepository;
import com.pjs.golf.fields.repository.querydsl.FieldsJpaQuerydslSupport;
import com.pjs.golf.fields.service.FieldsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FieldsServiceImpl implements FieldsService {

    private final FieldsJpaRepository fieldsJpaRepository;
    private final FieldsJpaQuerydslSupport fieldsJpaQuerydslSupport;
    @Override
    @Transactional(readOnly = true)
    public Fields createField(Fields fields) {
        return fieldsJpaRepository.save(fields);
    }

    @Override
    public Page<Fields> getFieldList(SearchDto city, Pageable pageable) {
        return fieldsJpaQuerydslSupport.getFieldsListBySearCh(city,pageable);
    }

    @Override
    public Fields getFieldSingle(int id) {
        return fieldsJpaRepository.findById(id)
                .orElseThrow(()->new NoSuchDataCustomException());
    }

    @Override
    public void deleteField(Fields fields) {
        fieldsJpaRepository.delete(fields);
    }
}
