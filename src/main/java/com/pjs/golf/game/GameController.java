package com.pjs.golf.game;


import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.WebCommon;
import com.pjs.golf.common.annotation.CurrentUser;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import com.pjs.golf.common.exception.InCorrectStatusCustomException;
import com.pjs.golf.common.exception.NoSuchDataCustomException;
import com.pjs.golf.common.exception.PermissionLimitedCustomException;
import com.pjs.golf.game.dto.GameDto;
import com.pjs.golf.game.dto.GameStatus;
import com.pjs.golf.game.entity.Game;
import com.pjs.golf.game.service.GameService;
import com.pjs.golf.game.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

@RestController
@RequestMapping(value = "/api/game",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ScoreService scoreService;

    private final WebCommon webCommon;

    /**
     * 경기 목록조회
     * <pre>
     * 조회 기간이나 검색어를 받아서 해당하는 목록을 조회하는 컨트롤러
     * 조건이 없을 경우 null을 받아야 됨.
     * 검색기간의 형식은 2023-01-01T00:00:00
     * </pre>
     */
    @GetMapping
    public ResponseEntity getGameList(
            Pageable pageable,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchTxt,
            PagedResourcesAssembler<Game> assembler,
            @CurrentUser Account account
    ){
        SearchDto search = SearchDto.builder()
                .startDate((webCommon.localDateToLocalDateTime(startDate,"startDate")))
                .endDate((webCommon.localDateToLocalDateTime(endDate,"endDate")))
                .SearchTxt(searchTxt)
                .build();

        Page<Game> games = gameService.getGameList(search,pageable);
        CollectionModel pageResources = gameService.getPageReesources(assembler, games);

        return ResponseEntity.ok().body(pageResources);
    }


    /**
     * 경기 단일조회
     * <pre>
     * 해당하는 게임의 id 값을 받아서 해당 게임의 상세내용을 조회한다.
     * @return 200 | 204
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity getGameInfo(
            @PathVariable int id,
            @CurrentUser Account account){

        try {
            Game game = gameService.getGameInfo(id);

            EntityModel resource = gameService.getResource(game, account);

            return ResponseEntity.ok().body(resource);

        }catch (NoSuchDataCustomException e){
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * 경기 등록
     * @return 200 | 400
     * @param gameDto GameDto
     * */

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity creatGame(
            @RequestBody GameDto gameDto,
            Errors errors,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        try{
            Game savedGame = gameService.createGame(gameDto, account);
            EntityModel resource = gameService.getResource(savedGame, account);
            return new ResponseEntity(resource, HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *  경기 수정
     * @return 200 | 400
     * */
    @PutMapping()
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity updateGame(
            @RequestBody GameDto gameDto,
            Errors errors,
            @CurrentUser Account account
    ){
        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        try {
            Game updatedGame = gameService.updateGame(gameDto, account);
            EntityModel resource = gameService.getResource(updatedGame, account);
            return ResponseEntity.ok().body(resource); // 200
        }catch (PermissionLimitedCustomException e){
            return new ResponseEntity(HttpStatus.FORBIDDEN); // 403
        }catch (NoSuchDataCustomException e){
            return ResponseEntity.notFound().build(); // 404
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();  // 500
        }
    }


    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity enroll(
            @PathVariable int id,
            @CurrentUser Account account) {
        try {
            Game enrolledGame = gameService.enrollGame(id, account);
            EntityModel resource = gameService.getResource(enrolledGame, account);
            return ResponseEntity.ok().body(resource); // 200
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (AlreadyExistSuchDataCustomException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("msg", e.getMessage()));// 400
        }catch (InCorrectStatusCustomException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("msg", e.getMessage()));// 400
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }

    /**
     * 등록 마감하기
     * OPEN -> CLOSED
     * */
    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity closeGame(
            @PathVariable int id,
            @CurrentUser Account account) {
        try {
            Game enrolledGame = gameService.updateFowrdStatusGame(id, account, GameStatus.CLOSED);
            EntityModel resource = gameService.getResource(enrolledGame, account);
            return ResponseEntity.ok().body(resource); // 200
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (PermissionLimitedCustomException e) {
            return new ResponseEntity(HttpStatus.FORBIDDEN); // 403
        }catch (InCorrectStatusCustomException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("msg", e.getMessage()));// 400
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }

    /**
     * 경기 시작하기
     * ClOSED -> PLAYING
     * */
    @PutMapping("/{id}/play")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity playGame(
            @PathVariable int id,
            @CurrentUser Account account) {
        int[] num_list ={1,2,3,45,5,12};
        OptionalInt result = Arrays.stream(num_list).filter(e-> e < 0  ).findFirst();
        try {
            Game enrolledGame = gameService.updateFowrdStatusGame(id, account, GameStatus.PLAYING);
            EntityModel resource = gameService.getResource(enrolledGame, account);
            return ResponseEntity.ok().body(resource); // 200
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (PermissionLimitedCustomException e) {
            return new ResponseEntity(HttpStatus.FORBIDDEN); // 403
        }catch (InCorrectStatusCustomException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("msg", e.getMessage()));// 400
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }

    /**
     * 경기 시작
     * PLAYING -> END
     * */
    @PutMapping("/{id}/end")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity endGame(
            @PathVariable int id,
            @CurrentUser Account account) {
        try {
            Game enrolledGame = gameService.updateFowrdStatusGame(id, account, GameStatus.END);
            EntityModel resource = gameService.getResource(enrolledGame, account);
            return ResponseEntity.ok().body(resource); // 200
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build(); // 404
        } catch (PermissionLimitedCustomException e) {
            return new ResponseEntity(HttpStatus.FORBIDDEN); // 403
        }catch (InCorrectStatusCustomException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("msg", e.getMessage()));// 400
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);  // 500
        }
    }


    /**
     * 조편성 하기
     * */

    @PostMapping("{id}/score/group")
    public ResponseEntity groupingPlayers(
            @PathVariable int id,
            @CurrentUser Account account) {
//        List scores = gameService.draw(id, account);
        return null;
    }

    /**
     * 경기별 점수 보기
     * */
    @GetMapping("{id}/score")
    public ResponseEntity getScores(@PathVariable int id) {
        List scores = scoreService.getScoreList(id);
        return null;
    }



}
