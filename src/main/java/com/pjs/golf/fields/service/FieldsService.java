package com.pjs.golf.fields.service;

import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FieldsService {
    Fields createField(Fields fields);

    Page<Fields> getFieldList(SearchDto city, Pageable pageable);

    Fields getFieldSingle(int id);

    void deleteField(Fields fields);
}
