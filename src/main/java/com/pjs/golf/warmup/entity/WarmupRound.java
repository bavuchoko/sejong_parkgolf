package com.pjs.golf.warmup.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pjs.golf.warmup.entity.id.WarmupRoundId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupRound {


    @EmbeddedId
    private WarmupRoundId id;

    private int hole;

    @OneToMany
    @JoinColumns({
            @JoinColumn(name = "game_id"),
            @JoinColumn(name = "rounding_id")
    })
    private List<WarmupScore> scores;

    public void setCScoresForInitRounding(List list) {
        this.scores=list;
    }

}
