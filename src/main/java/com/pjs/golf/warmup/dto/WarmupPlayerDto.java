package com.pjs.golf.warmup.dto;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.warmup.entity.WarmupGame;
import lombok.*;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupPlayerDto {
    private WarmupGame game;
    private Account player;
    private int score;
}
