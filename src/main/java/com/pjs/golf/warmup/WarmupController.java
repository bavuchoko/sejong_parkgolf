package com.pjs.golf.warmup;


import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.service.AccountService;
import com.pjs.golf.common.WebCommon;
import com.pjs.golf.common.annotation.CurrentUser;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.exception.InCorrectStatusCustomException;
import com.pjs.golf.common.exception.NoSuchDataCustomException;
import com.pjs.golf.common.exception.PermissionLimitedCustomException;
import com.pjs.golf.sse.SSEService;
import com.pjs.golf.warmup.dto.WarmupGameDto;
import com.pjs.golf.warmup.dto.WarmupScoreDto;
import com.pjs.golf.warmup.entity.WarmupGame;
import com.pjs.golf.warmup.service.WarmupGameMapper;
import com.pjs.golf.warmup.service.WarmupGameService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value = "/api/warmup",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class WarmupController {

    private final WebCommon webCommon;
    private final WarmupGameService warmupGameService;
    private final AccountService accountService;
    private final SSEService sseService;

    /**
     * 친선경기 목록조회
     * */
    @GetMapping
    public ResponseEntity getWarmupGameList(
            Pageable pageable,
            @CurrentUser Account account,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            PagedResourcesAssembler<WarmupGame> assembler) {
        SearchDto search = SearchDto.builder()
                .startDate((webCommon.localDateToLocalDateTime(startDate,"startDate")))
                .endDate((webCommon.localDateToLocalDateTime(endDate,"endDate")))
                .build();
        Page<WarmupGame> warmupGames = warmupGameService.getWarmupGameList(search, pageable);
        CollectionModel pageResources = warmupGameService.getPageReesources(assembler, warmupGames, account);
        return ResponseEntity.ok().body(pageResources);
    }

    /**
     * 친선 경기 생성
     * */

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity createWarmup(
            @CurrentUser Account account,
            @RequestBody WarmupGameDto warmupGameDto,
            Errors errors) {

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        try {
            warmupGameDto.setCreatedBy(account);
            WarmupGame warmupGame = warmupGameService.createWarmup(warmupGameDto);
            EntityModel resource = warmupGameService.getResource(warmupGame, account);
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 친선 경기 참가
     * */

    @PutMapping("/join/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity joinWarmup(
            @PathVariable Long id,
            @CurrentUser Account account){
        if (account == null) {
            return ResponseEntity.badRequest().body("로그인이 필요 합니다.");
        }
        warmupGameService.joinWarmupGame(id, account);

        WarmupGame gameInfo = warmupGameService.getGameInfo(id);
        //SSSE subscibe
        EntityModel entityModel =EntityModel.of(gameInfo);
        sseService.broadCast(id,entityModel);

        return null;
    }

    /**
     * 친선 경기 강제퇴장
     * */

    @PutMapping("/expel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity expelWarmup(
            @PathVariable Long id,
            @CurrentUser Account account,
            @RequestBody Account target){
        if (account == null) {
            return ResponseEntity.badRequest().body("로그인이 필요 합니다.");
        }
        try {

            WarmupGame gameInfo = warmupGameService.getGameInfo(id);
            //SSSE subscibe
            EntityModel entityModel =EntityModel.of(gameInfo);
            sseService.broadCast(id,entityModel);

            warmupGameService.expelPlayer(id, account, target);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 친선 경기 시작
     * */
    @PutMapping("/play/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity startWarmup(
            @PathVariable Long id,
            @CurrentUser Account account) {

        try {
            warmupGameService.startWarmup(id, account);

            WarmupGame gameInfo = warmupGameService.getGameInfo(id);

            //SSSE subscibe
            EntityModel entityModel =EntityModel.of(WarmupGameMapper.Instance.toDtoWithoutRoles(gameInfo));
            sseService.broadCast(id,entityModel);

            return ResponseEntity.ok().body(HttpStatus.OK);
        } catch (PermissionLimitedCustomException | InCorrectStatusCustomException | NoSuchDataCustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 친선경기 단일조회
     * */
    @GetMapping("/{id}")
    public ResponseEntity getWarmupGameInfo(
            @PathVariable int id,
            @CurrentUser Account account) {

        try {
            WarmupGame warmupGame = warmupGameService.getGameInfo(id);
            EntityModel resource = warmupGameService.getResource(warmupGame, account);
            return ResponseEntity.ok().body(resource);
        }catch (NoSuchDataCustomException e){
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * 친선경기 종료
     * */
    @PutMapping("/finish/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity endWarmupGame(
            @PathVariable Long id,
            @CurrentUser Account account) {

        try {
            WarmupGame gameInfo = warmupGameService.getGameInfo(id);

            if(account.equals(gameInfo.getCreatedBy())){
                warmupGameService.finishGame(id);

                //SSSE subscibe
                EntityModel entityModel =EntityModel.of(WarmupGameMapper.Instance.toDtoWithoutRoles(gameInfo));
                sseService.broadCast(id,entityModel);

                return new ResponseEntity(HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }catch (NoSuchDataCustomException e){
            return ResponseEntity.noContent().build();
        }
    }


    /**
     * 친선경기 삭제
     * */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity deleteWarmup(
            @PathVariable Long id,
            @CurrentUser Account account) {

        try {
            WarmupGame gameInfo = warmupGameService.getGameInfo(id);
            if(account.equals(gameInfo.getCreatedBy())){
                warmupGameService.deleteWarmup(id);
                return new  ResponseEntity(HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
    
    /**
     * 새 라운드 생성하기
     * */
    @PutMapping("/rounding/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity createRounding(
            @PathVariable Long id,
            @CurrentUser Account account){
        try {
            warmupGameService.createRounding(id, account);
            WarmupGame gameInfo = warmupGameService.getGameInfo(id);
            //SSSE subscibe
            EntityModel entityModel =EntityModel.of(WarmupGameMapper.Instance.toDtoWithoutRoles(gameInfo));
            sseService.broadCast(id,entityModel);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 점수입력
     * */
    @PostMapping("/score/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity insertScore(
            @PathVariable Long id,
            @RequestBody WarmupScoreDto warmupScoreDto,
            Errors errors,
            @CurrentUser Account account){
        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }
        try {
            WarmupGame gameInfo = warmupGameService.getGameInfo(id);
            if(account.equals(gameInfo.getCreatedBy())) {
                warmupGameService.insertScore(warmupScoreDto);

                //SSSE subscibe
                EntityModel entityModel =EntityModel.of(WarmupGameMapper.Instance.toDtoWithoutRoles(gameInfo));
                sseService.broadCast(id,entityModel);

                return new ResponseEntity(HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * 점수수정
     * */
    @PutMapping("/score")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity updateScore(
            @RequestBody WarmupScoreDto warmupScoreDto,
            Errors errors,
            @CurrentUser Account account){
        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }
        try {
            Long id = warmupScoreDto.getId().getRounding().getId().getGame().getId();
            WarmupGame gameInfo = warmupGameService.getGameInfo(id);
            if(account.equals(gameInfo.getCreatedBy())) {
                warmupGameService.updateScore(warmupScoreDto);

                //SSSE subscibe
                EntityModel entityModel =EntityModel.of(WarmupGameMapper.Instance.toDtoWithoutRoles(gameInfo));
                sseService.broadCast(id,entityModel);

                return new ResponseEntity(HttpStatus.OK);
            }else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * SSE
     * 친선 경기  구독
     * */
    @GetMapping(path = "/subscribe", produces = "text/event-stream; charset=UTF-8")
    public SseEmitter subscribeWarmup(
            @RequestParam(required = true) Long id,
            @CurrentUser Account account) {
       return sseService.subscribe(id, account);
    }


}