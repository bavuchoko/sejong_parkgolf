package com.pjs.golf.warmup.dto;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.warmup.entity.id.WarmupScoreId;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupScoreDto {

    private WarmupScoreId id;
    private int hit;
}
