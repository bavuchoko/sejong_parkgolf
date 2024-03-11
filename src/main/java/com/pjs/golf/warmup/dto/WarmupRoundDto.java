package com.pjs.golf.warmup.dto;

import com.pjs.golf.warmup.entity.WarmupScore;
import com.pjs.golf.warmup.entity.id.WarmupRoundId;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupRoundDto {

    private WarmupRoundId id;

    private int hole;

    private List<WarmupScore> scores;
}
