package com.pjs.golf.game.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.ModelMapperUtils;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.game.dto.GameDto;
import com.pjs.golf.game.dto.GameStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "opener_id")
    private Account opener;

    @ManyToOne
    @JoinColumn(name = "field_id" )
    private Fields fields;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Column(nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;


    @ManyToMany
    @JoinTable(name = "game_players")
    private List<Account> players;

    private int rounding;
    private int playerCount;
    private String dayKor;
    private String detail;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    public void updateGame(GameDto gameDto) {
        this.opener = gameDto.getOpener();
        this.modifyDate = LocalDateTime.now();
        this.playDate = gameDto.getPlayDate();
        this.dayKor = gameDto.getDayKor();
        this.detail = gameDto.getDetail();
        this.status = gameDto.getStatus();
    }

    public void enrollGame(Game game, Account account) {
        this.players.add(account);
        this.playerCount = game.getPlayerCount()+1;
    }


    public void updateStat(GameStatus stat) {
        this.status = stat;
    }



}
