package com.pjs.golf.warmup.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.game.dto.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarmupGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;


    @ManyToOne
    @JoinColumn(name = "createdBy")
    private Account createdBy;

    @ManyToMany()
    private List<Account> players;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonBackReference
    private List<WarmupRound> rounds;

    private int holeCount;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Transient
    private String[] names;

    @Transient
    private int[] totalHits;

    @ManyToOne
    private Fields field;

    public void calculateTotalHits() {
        if (rounds != null && players != null) {
            int numPlayers = players.size();

            // 각 플레이어별로 총 히트 수를 저장할 배열 초기화
            totalHits = new int[numPlayers];

            for (WarmupRound round : rounds) {
                List<WarmupScore> scores = round.getScores();

                for (WarmupScore score : scores) {
                    String playerName = score.getId().getPlayer().getName();
                    int playerIndex = findPlayerIndex(playerName);

                    if (playerIndex != -1) {
                        totalHits[playerIndex] += score.getHit();
                    }
                }
            }
        }
    }

    private int findPlayerIndex(String playerName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                return i;
            }
        }
        return -1; // 플레이어를 찾지 못한 경우
    }

    public void changeStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }


}
