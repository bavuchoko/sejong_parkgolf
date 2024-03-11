package com.pjs.golf.game.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.game.entity.id.ScoreId;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ScoreId.class)
public class Score {

    @Id
    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Id
    @OneToOne
    @JoinColumn(name = "player_id")
    private Account player;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roundId;

    private int hole;

    private int hit;  //타수

    private int point;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;




}
