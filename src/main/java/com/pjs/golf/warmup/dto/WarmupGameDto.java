package com.pjs.golf.warmup.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.game.dto.GameStatus;
import com.pjs.golf.warmup.entity.WarmupRound;
import lombok.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupGameDto {

    private Long id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime playDate;

    private Account createdBy;

    private List<Account> players;

    private List<WarmupRound> rounds;

    private int holeCount;

    private GameStatus status;

    private String[] names;

    private int[] totalHits;

    private Fields field;

}
