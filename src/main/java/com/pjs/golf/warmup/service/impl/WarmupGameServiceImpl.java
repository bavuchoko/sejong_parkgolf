package com.pjs.golf.warmup.service.impl;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.service.AccountService;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.exception.InCorrectStatusCustomException;
import com.pjs.golf.common.exception.NoSuchDataCustomException;
import com.pjs.golf.common.exception.PermissionLimitedCustomException;
import com.pjs.golf.game.dto.GameStatus;
import com.pjs.golf.warmup.WarmupController;
import com.pjs.golf.warmup.dto.WarmupGameDto;
import com.pjs.golf.warmup.dto.WarmupScoreDto;
import com.pjs.golf.warmup.entity.WarmupGame;
import com.pjs.golf.warmup.entity.WarmupRound;
import com.pjs.golf.warmup.entity.WarmupScore;
import com.pjs.golf.warmup.entity.id.WarmupRoundId;
import com.pjs.golf.warmup.entity.id.WarmupScoreId;
import com.pjs.golf.warmup.repository.WarmupGameJpaRepository;
import com.pjs.golf.warmup.repository.WarmupRoundJpaRepository;
import com.pjs.golf.warmup.repository.WarmupScoreJpaRepository;
import com.pjs.golf.warmup.repository.querydsl.WarmupGameQuerydslSupport;
import com.pjs.golf.warmup.service.WarmupGameMapper;
import com.pjs.golf.warmup.service.WarmupGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class WarmupGameServiceImpl implements WarmupGameService {



    private final AccountService accountService;
    private final WarmupGameJpaRepository warmupGameJpaRepository;
    private final WarmupGameQuerydslSupport warmupGameQuerydslSupport;
    private final WarmupRoundJpaRepository warmupRoundJpaRepository;
    private final WarmupScoreJpaRepository warmupScoreJpaRepository;

    @Override
    public Page<WarmupGame> getWarmupGameList(SearchDto search, Pageable pageable) {
        return warmupGameQuerydslSupport.getWarumupGameListBetweenDate(search, pageable);
    }

    /***
     * 친선경기의 참가자는 Account 엔티티 이지만 temp_이름의 형태로 중복되지 않게 해당하는 기존 Account 엔티티를 찾거나 없으면 생성하여 넣어주려한다.
     */

    @Override
    @Transactional
    public WarmupGame createWarmup(WarmupGameDto warmupGameDto) {

        //클라이언트에서 입력한 참가자 이름들.
        String[] names = warmupGameDto.getNames();


        //Temp_이름 형태의 Account 컬랙션으로 젼환
        List<Account> players = organizeToTempUser(warmupGameDto.getCreatedBy(), names);


        warmupGameDto.setPlayDate(LocalDateTime.now());
        warmupGameDto.setStatus(GameStatus.OPEN);
        warmupGameDto.setPlayers(players);
        if(warmupGameDto.getField().getId() ==null) warmupGameDto.setField(null);
        WarmupGame gameEntity = warmupGameJpaRepository.save(WarmupGameMapper.Instance.toEntity(warmupGameDto));

        //게임 최초 등록시 첫번째(0번째) 라운드저장

        return gameEntity;
    }

    @Override
    @Transactional
    public void startWarmup(Long id, Account account) throws Exception {
        WarmupGame gameEntity = warmupGameJpaRepository.findById(id).orElseThrow(()->
                new NoSuchDataCustomException("없는 데이터")
        );
        try {
            if(gameEntity.getPlayers().size()>1){
                if (gameEntity.getCreatedBy().equals(account)) {
                    //라운드 생성
                    WarmupRound rounding = createRound(gameEntity, 0);
                    //새 라운드에 각 선수들 개별 점수 0점으로 초기화
                    initRoundBysetScores(gameEntity.getPlayers(), rounding);
                    gameEntity.changeStatus(GameStatus.PLAYING);
                } else {
                    throw new PermissionLimitedCustomException("권한이 없습니다.");
                }
            }else{
                throw new InCorrectStatusCustomException("플레이어가 없습니다.");
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }


    private void initRoundBysetScores(List<Account> players, WarmupRound rounding) {

        List scores = players.stream().map(player-> WarmupScore.builder().id(new WarmupScoreId(rounding,player)).hit(0).build()).collect(Collectors.toList());
        warmupScoreJpaRepository.saveAll(scores);
    }



    private WarmupRound createRound(WarmupGame gameEntity, int roundIndex) {
        WarmupRound rounding = WarmupRound.builder().id(new WarmupRoundId(gameEntity, roundIndex)).hole(0).build();
        warmupRoundJpaRepository.save(rounding);
        return rounding;
    }

    private List<Account> organizeToTempUser(Account createdBy, String[] names) {
        //기존에 temp_이름 으로 존재하는 Account 엔티티들
        List<Account> registeredAccounts = accountService.getTempUsersByUserNames(Arrays.asList(names));

        //등록되어 있지 않은 이름 -> temp_이름 으로 Account 생성해주어야 할 이름들
        List<String> unRegisteredUsernames =  Arrays.stream(names).filter(
                name -> registeredAccounts.stream()
                        .noneMatch(account -> account.getName().equals(name))).collect(Collectors.toList());

        //unRegisteredUsernames 로 새로 생성해준 Account 들
        List<Account> newAccount = accountService.createUserIfDosenExist(unRegisteredUsernames);

        //등록 & 생성 된 account 들 합침.
        registeredAccounts.addAll(newAccount);

        //클라이언트가 입력한 이름 순서대로 정렬을 편하게 하기 위해 registeredAccounts 리스트를 이름을 기준으로 매핑한 맵 생성
        Map<String, Account> accountMap = registeredAccounts.stream()
                .collect(Collectors.toMap(Account::getName, account -> account));

        //처음 입력한 순서대로 정렬해줌. :: Account 객체이며 클라이언트가 입력한 순서대로 정렬된 리스트
        List<Account> players = Arrays.stream(names)
                        .map(accountMap::get).collect(Collectors.toList());

        //등록자 자동 참가 => 입력한 이름중 첫번째 사람을 제외하지 않으면
        if(createdBy != null && players.size() < 4){
            players = players.subList(1,players.size());
            players.add(0, createdBy);
        }else if(createdBy != null && players.size() >= 4){
            players = players.subList(1,4);
            players.add(0, createdBy);
        }
        return players;
    }

    public EntityModel getResource(WarmupGame warmupGame, Account account) {
        warmupGame.calculateTotalHits();
        WarmupGameDto target = WarmupGameMapper.Instance.toDto(warmupGame);
        WebMvcLinkBuilder selfLink = linkTo(WarmupController.class).slash(target.getId());
        EntityModel resource = EntityModel.of(target);
        if(target.getCreatedBy().equals(account)){
            resource.add(linkTo(WarmupController.class).slash("score").withRel("update"));
        }
        resource.add(selfLink.withRel("query-content"));

        return resource;
    }

    @Override
    public CollectionModel getPageReesources(PagedResourcesAssembler<WarmupGame> assembler, Page<WarmupGame> warmupGames, Account account) {
        return assembler.toModel(warmupGames, entity -> {
            EntityModel<WarmupGameDto> entityModel = EntityModel.of(WarmupGameMapper.Instance.toDtoForResource(entity))
                    .add(linkTo(WarmupController.class).slash(entity.getId()).withSelfRel());
            if (account != null && entity.getCreatedBy().equals(account)) {
                entityModel.add(linkTo(WarmupController.class).slash(entity.getId()).withRel("update"));
            }
            return entityModel.add(Link.of("/docs/asciidoc/index.html#create-game-api").withRel("profile"));
        });
    }


    @Override
    public WarmupGame getGameInfo(long id) {
        return warmupGameJpaRepository.findById(id).orElseThrow(()->new NoSuchDataCustomException());
    }

    /**
     * 점수 입력
     * */
    @Override
    @Transactional
    public void insertScore(WarmupScoreDto warmupScoreDto) {
        WarmupScore entity = warmupScoreJpaRepository.findById(warmupScoreDto.getId()).orElseThrow(()->new NoSuchDataCustomException());
        entity.updateHit(warmupScoreDto.getHit());
//        WarmupScore entity = WarmupScoreMapper.Instance.toEntity(warmupScoreDto);
    }

    /**
     * 라운드 진행
     * */
    @Override
    public void createRounding(Long id, Account account) {
        WarmupGame entity = warmupGameJpaRepository.findById(id).orElseThrow(()->new NoSuchDataCustomException());

        if (entity.getCreatedBy().equals(account)) {
            //다음 라운드 생성
            WarmupRound round = createRound(entity, entity.getRounds().size());
            initRoundBysetScores(entity.getPlayers(), round);
        }else{
            throw new PermissionLimitedCustomException("AuthorityException");
        }
    }

    /**
     * 점수 수정
     * */
    @Override
    @Transactional
    public void updateScore(WarmupScoreDto warmupScoreDto) {
        WarmupScoreId id = warmupScoreDto.getId();
        WarmupScore score = warmupScoreJpaRepository.findById(id).orElseThrow( () -> new NoSuchDataCustomException() );
        score.updateHit(warmupScoreDto.getHit());
    }

    /**
     * 게임 삭제
     * */
    @Override
    @Transactional
    public void deleteWarmup(Long id) {
        List socres = warmupScoreJpaRepository.selectWarmupGameById(id);
        warmupScoreJpaRepository.deleteAllInBatch(socres);
        List<WarmupRound> rounds = warmupRoundJpaRepository.selectWarmupRoundByGameId(id);
        warmupRoundJpaRepository.deleteAllInBatch(rounds);
        warmupGameJpaRepository.deleteById(id);
    }

    /**
     * 게임 종료
     * */
    @Override
    @Transactional
    public WarmupGame finishGame(Long id) {
        WarmupGame warmupGame = warmupGameJpaRepository.findById(id).orElseThrow(() -> new NoSuchDataCustomException());
        warmupGame.changeStatus(GameStatus.END);
        return null;
    }

    
    /**
     * 게임 참가
     * */
    @Override
    @Transactional
    public void joinWarmupGame(Long id, Account account) {
        WarmupGame entity = warmupGameJpaRepository.findById(id).orElseThrow(
                ()-> new NoSuchDataCustomException()
        );
        if(GameStatus.OPEN != entity.getStatus()){
            throw new InCorrectStatusCustomException("참가할 수 없습니다.");
        }

        if (entity.getPlayers().stream().anyMatch(e -> e.equals(account))) {
            throw new InCorrectStatusCustomException("Already Enrolled");
        }

        if (entity.getPlayers().size() >= 4) {
            throw new InCorrectStatusCustomException("더이상 참가할 수 없습니다.");
        }else{
            entity.getPlayers().add(account);
        }
    }

    @Override
    @Transactional
    public void expelPlayer(Long id, Account account, Account target) {
        WarmupGame entity = warmupGameJpaRepository.findById(id).orElseThrow(
                ()-> new NoSuchDataCustomException()
        );
        if(GameStatus.OPEN != entity.getStatus()){
            throw new InCorrectStatusCustomException("statusNotAllowedException");
        }
        if(entity.getCreatedBy().equals(target)){
            throw new InCorrectStatusCustomException("hostExpelException");
        }
        if(entity.getCreatedBy().equals(account)) {
            entity.getPlayers().remove(target);
        }else{
            if(!account.equals(target))  throw new InCorrectStatusCustomException("selfExpelAllowedException");
            else entity.getPlayers().remove(target);
        }
    }
}
