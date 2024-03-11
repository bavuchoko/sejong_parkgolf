package com.pjs.golf.warmup.entity;

import com.pjs.golf.warmup.entity.id.WarmupScoreId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupScore {

    @EmbeddedId
    private WarmupScoreId id;

    private int hit;

    public void updateHit(int hit) {
        this.hit = hit;
    }
}
