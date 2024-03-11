package com.pjs.golf.fields.dto;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.ModelMapperUtils;
import com.pjs.golf.fields.entity.Fields;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldsDto {

    private Integer id;

    @NotNull
    private Account register;

    @NotNull
    private String name;
    private String city;

    @NotNull
    private String address;

    private int course;
    private int holes;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

}
