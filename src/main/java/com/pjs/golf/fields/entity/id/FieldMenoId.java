package com.pjs.golf.fields.entity.id;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.fields.entity.Fields;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldMenoId implements Serializable {

    private static final long serialVersionUID= 1L;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Fields fields;

    private int round;

    private int hole;

    @ManyToOne
    private Account player;
}
