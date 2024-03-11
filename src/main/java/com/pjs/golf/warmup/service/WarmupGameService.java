package com.pjs.golf.warmup.service;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.game.entity.Game;
import com.pjs.golf.warmup.dto.WarmupGameDto;
import com.pjs.golf.warmup.dto.WarmupRoundDto;
import com.pjs.golf.warmup.dto.WarmupScoreDto;
import com.pjs.golf.warmup.entity.WarmupGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;

public interface WarmupGameService {

    Page<WarmupGame> getWarmupGameList(SearchDto search, Pageable pageable);
    WarmupGame createWarmup(WarmupGameDto warmupGameDto);
    void startWarmup(Long id, Account account) throws Exception;

    EntityModel getResource(WarmupGame warmupGame, Account accoun);

    CollectionModel getPageReesources(PagedResourcesAssembler<WarmupGame> assembler, Page<WarmupGame> games, Account account);

    WarmupGame getGameInfo(long id);

    void insertScore(WarmupScoreDto warmupScoreDto);

    void createRounding(Long warmupGameDto, Account account);

    void updateScore(WarmupScoreDto warmupScoreDto);

    void deleteWarmup(Long id);

    WarmupGame finishGame(Long id);

    void joinWarmupGame(Long id, Account account);

    void expelPlayer(Long id, Account account, Account targer);
}
