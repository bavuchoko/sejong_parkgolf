package com.pjs.golf.game.service;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.game.dto.GameDto;
import com.pjs.golf.game.dto.GameStatus;
import com.pjs.golf.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public interface GameService {

    Game getGameInfo(long id);

    /**
     * 목록조회
     * @param search SearchDto
     * @param pageable Pageable
     * */
    Page<Game> getGameList(SearchDto search, Pageable pageable);

    Game createGame(GameDto gameDto, Account account);

    Game updateGame(GameDto gameDto, Account account);

    CollectionModel getPageReesources(PagedResourcesAssembler<Game> assembler, Page<Game> games);


    EntityModel getResource(Game game, Account account);

    Game enrollGame(long id, Account account);

    Game updateFowrdStatusGame(long id, Account account, GameStatus gameStatus);

}
