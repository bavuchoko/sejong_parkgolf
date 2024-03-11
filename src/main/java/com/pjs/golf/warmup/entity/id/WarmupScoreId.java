package com.pjs.golf.warmup.entity.id;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.warmup.entity.WarmupRound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WarmupScoreId implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "game_id")
    @JoinColumn(name = "rounding_id")
    @JsonBackReference
    private WarmupRound rounding;

    @ManyToOne
    private Account player;
}
