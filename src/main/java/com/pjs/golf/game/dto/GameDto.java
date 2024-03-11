package com.pjs.golf.game.dto;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.ModelMapperUtils;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.game.entity.Game;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GameDto {

    private Long id;

    @NotNull
    private String title;

    private Account opener;

    private Fields fields;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;
    @NotNull
    private LocalDateTime playDate;

    private List<Account> players;

    private int rounding;
    private int playerCount;
    private String dayKor;
    private String detail;
    private GameStatus status;
    public void whatIsDay(LocalDateTime time) {
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        this.dayKor = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }

}
