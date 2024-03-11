package com.pjs.golf.warmup.entity.id;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.warmup.entity.WarmupGame;
import com.pjs.golf.warmup.entity.WarmupRound;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class WarmupRoundId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private WarmupGame game;

    private int rounding;
}
