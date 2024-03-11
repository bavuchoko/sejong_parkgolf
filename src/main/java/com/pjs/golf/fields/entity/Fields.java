package com.pjs.golf.fields.entity;


import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.ModelMapperUtils;
import com.pjs.golf.fields.dto.FieldsDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Fields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;

    @ManyToOne
    private Account register;

    @Column(nullable = false)
    private String name;
    private String city;

    @Column(nullable = false)
    private String address;


    @ColumnDefault("4")
    private int course;

    @ColumnDefault("9")
    private int holes;

    @Column(nullable = false)
    private LocalDateTime createDate;
    @Column(nullable = true)
    private LocalDateTime modifyDate;

}
