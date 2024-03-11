package com.pjs.golf.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class SearchDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String SearchTxt;
}
