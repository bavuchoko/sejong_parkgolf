package com.pjs.golf.account;

import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.AccountRole;
import com.pjs.golf.account.entity.Gender;
import com.pjs.golf.account.service.AccountMapper;
import com.pjs.golf.account.service.AccountService;
import com.pjs.golf.common.annotation.CurrentUser;
import com.pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import com.pjs.golf.config.filter.TokenFilter;
import com.pjs.golf.config.token.TokenManager;
import com.pjs.golf.config.utils.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/user",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Autowired
    CookieUtil cookieUtil;


    @GetMapping
    public ResponseEntity loadUserList(Pageable pageable, PagedResourcesAssembler<Account> assembler){

        Page<Account> page = accountService.loadUserList(pageable);

        var pageResources = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(AccountController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index/html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    
    //로그인
    @PostMapping("/authentication")
    public ResponseEntity authenticate(
            @Valid @RequestBody AccountDto accountDto,
            Errors errors,
            HttpServletResponse response,
            HttpServletRequest request) {

        if(errors.hasErrors()){
            return badRequest(errors);
        }
        try {
            String accessToken = accountService.authorize(accountDto,response, request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            Map responseMap = accountService.getResponseMap(accessToken);
            return new ResponseEntity(responseMap, httpHeaders, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<>("fail to login",HttpStatus.BAD_REQUEST);
        }
    }


    //회원가입
    @PostMapping("/create")
    public ResponseEntity creatAccount(
            @Valid @RequestBody AccountDto accountDto,
            Errors errors,
            HttpServletResponse response,
            HttpServletRequest request) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        try {
            accountDto.setRoles(Set.of(AccountRole.USER));
            accountDto.setJoinDate(LocalDateTime.now());
            Character lastBirth = accountDto.getBirth().charAt( accountDto.getBirth().length() - 1);
            if('1'== lastBirth){
                accountDto.setGender(Gender.MALE);
            }else if('2'== lastBirth){
                accountDto.setGender(Gender.FEMALE);
            }
            accountDto.setRoles(Set.of(AccountRole.USER));
            Account account = AccountMapper.Instance.toEntity(accountDto);
            accountService.saveAccount(account);

            String accessToken = accountService.authorize(accountDto, response, request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            Map responseMap = accountService.getResponseMap(accessToken);

            return new ResponseEntity(responseMap, httpHeaders, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>("fail to create" ,HttpStatus.BAD_REQUEST);
        }catch (AlreadyExistSuchDataCustomException e){
            return new ResponseEntity<>("already existing id" ,HttpStatus.BAD_REQUEST);
        }
    }


    //Todo 중요!!
    /**
     * 쿠키에서 리프레쉬 토큰을 가져와서 토큰검증 후 리프레쉬토큰의 만료시간이 지나지 안았으면 새로운 토큰을 발급한다.
     * 실제 테스트 해보니 처음 정상적인 인가를 진행하면 쿠키에 refreshToken을 넣어주고 이후 요청에선 엑세스 토큰이 없어도 이 api를 요청하기만 하면
     * 새 토큰을 발급해버린다.
     * 어차피 쿠키에 refreshToken이 있는것은 정당한 토큰 갱신요청으로 볼 수 있으므로 갱신 처리를 하면 될지 아니면 accessToken이 없는 갱신 요청은 부적절한 요청으로 볼 것인지?
     *  if jwtAuthenticationEntryPoint 에서 401 처리를 할때 토큰이 없는경우를 구별해줘야 할 듯
     */

    @GetMapping("/reissue")
    public ResponseEntity reissue(HttpServletRequest request) {

        try{
            String accessToken = accountService.reIssueToken(request);
            Map responseMap = accountService.getResponseMap(accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            return new ResponseEntity(responseMap, httpHeaders, HttpStatus.OK);

        }catch (Exception e){
           return new ResponseEntity("fail to refresh token", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/tokenVaildation")
    public ResponseEntity valdationTimeCheck(@RequestHeader(name = "Authorization") String token) {
        if(StringUtils.hasText(token)){
            try {
                token = token.replace("Bearer ", "");
                if (accountService.validateToken(token)) {
                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    return new ResponseEntity(HttpStatus.FORBIDDEN);
                }
            } catch (ExpiredJwtException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);

            }
        }
        return new ResponseEntity("token is empty", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/logout")
    public void logout(HttpServletRequest req){
        accountService.logout(req);
    }

    @GetMapping("/validation")
    public String valdationTimeCheck(@CurrentUser Account account) {
        System.out.println(account.getName());
        return  "aa";
    }



    @GetMapping("/permitted")
    public String permitted(){
        return "everybody permiited";
    }


    @GetMapping("/admintest")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String admintest(){
        return "only admin permiited";
    }

    @GetMapping("/usertest")
    @PreAuthorize("hasAnyRole('USER')")
    public String usertest(){
        return "only user permiited";
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(AccountController.class).slash("/join").withRel("redirect")));
    }


}
