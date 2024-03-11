package com.pjs.golf.fields.entity;


import com.pjs.golf.fields.entity.id.FieldMenoId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldMeno {

    @EmbeddedId
    private FieldMenoId menoId;

    @Column(columnDefinition = "TEXT")
    private String memo;
}
