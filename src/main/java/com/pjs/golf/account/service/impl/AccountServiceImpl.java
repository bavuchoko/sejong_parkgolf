package com.pjs.golf.account.service.impl;


import com.pjs.golf.account.dto.AccountAdapter;
import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.AccountRole;
import com.pjs.golf.account.entity.Gender;
import com.pjs.golf.account.repository.AccountJpaRepository;
import com.pjs.golf.account.repository.querydsl.AccountQuerydslSupport;
import com.pjs.golf.account.service.AccountMapper;
import com.pjs.golf.account.service.AccountService;
import com.pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import com.pjs.golf.config.token.TokenManager;
import com.pjs.golf.config.token.TokenType;
import com.pjs.golf.config.utils.CookieUtil;
import com.pjs.golf.config.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {



    private final AccountJpaRepository accountJpaRepository;
    private final AccountQuerydslSupport accountQuerydslSupport;
    @Autowired
    CookieUtil cookieUtil;

    @Value("${spring.jwt.token-validity-in-seconds}")
    private long accessTokenValidityTime;

    private final TokenManager tokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;

    private final PasswordEncoder passwordEncoder;



    @Override
    public Account saveAccount(Account account) {

        accountJpaRepository.findByUsername(account.getUsername()).ifPresent(e->{
            throw new AlreadyExistSuchDataCustomException("Duplicated username");
        });
        account.overwritePassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountJpaRepository.save(account);
    }



    @Override
    public String authorize(AccountDto account, HttpServletResponse response, HttpServletRequest request) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
        String refreshToken = tokenManager.createToken(authentication, TokenType.REFRESH_TOKEN);
//        redisUtil.setData(refreshToken, WebCommon.getClientIp(request));

        Cookie refreshTokenCookie = cookieUtil.createCookie(TokenType.REFRESH_TOKEN.getValue(), refreshToken);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        refreshTokenCookie.setMaxAge((int)(accessTokenValidityTime * 12 * 7));
        response.addCookie(refreshTokenCookie);

        return accessToken;
    }

    @Override
    public String reIssueToken(HttpServletRequest request) {
        //쿠키에서 refreshToken을 꺼냄
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        String accessToken = null;
        //refresh토큰을 검증함
        if(StringUtils.hasText(refreshTokenInCookie) && tokenManager.validateToken(refreshTokenInCookie)){
            //refresh토큰으로 부터 인증객체 생성
            Authentication authentication = tokenManager.getAuthenticationFromRefreshToken(request);
            accessToken = tokenManager.createToken(authentication, TokenType.ACCESS_TOKEN);
            String a="";

            /**
             * todo
             * 갱신토큰의 갱신에 관한 로직 필요
             * 1. 매번 엑세스 토큰이 갱신될때마다 갱신토큰을 갱신할 것인지,
             * 2. 갱신토큰의 유효시간이 얼마 이하 일때만 갱신할 것인지.
             * 3. 갱신하지 않고 갱신토큰 만료시 새로 로그인을 요구할지.
             */
        }else{
            throw new IllegalArgumentException("No valid refreshToken");
        }
        return accessToken;
    }

    @Override
    public void logout(HttpServletRequest req) {

        /**
         * todo
         * 이미 발급된 엑세스 토큰은 어떻게 처리할 것인가.
         */
        if(null != cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue())){
        String refreshTokenInCookie = cookieUtil.getCookie(req, TokenType.REFRESH_TOKEN.getValue()).getValue();
        redisUtil.deleteData(refreshTokenInCookie);
        }
    }

    @Override
    public Page<Account> loadUserList(Pageable pagable){
        return this.accountJpaRepository.findAll(pagable);
    }

    @Override
    public Map getResponseMap(String accessToken) {
        Map responseMap = new HashMap<>();
        responseMap.put("status", HttpStatus.OK);
        responseMap.put("result", "success");
        responseMap.put("accessToken", accessToken);
        responseMap.put("id", tokenManager.getId(accessToken));
        responseMap.put("username", tokenManager.getUsername(accessToken));
        responseMap.put("name", tokenManager.getName(accessToken));
        responseMap.put("birth", tokenManager.getBirth(accessToken));
        responseMap.put("gender", tokenManager.getGender(accessToken));
        responseMap.put("joinDate", tokenManager.getJoinDate(accessToken));
        return responseMap;
    }

    @Override
    public boolean validateToken(String token) {
        return tokenManager.validateToken(token);
    }

    @Override
    public List createUserIfDosenExist(List<String> names) {
        List tempUsers = names.stream()
                .map(e -> AccountMapper.Instance.toEntity(AccountDto.builder()
                .name(e)
                .password(this.passwordEncoder.encode("temp_xxaareddfef"))
                .username("temp_"+e)
                .birth("6001011")
                .gender(Gender.MALE)
                .joinDate(LocalDateTime.now())
                .roles(Set.of(AccountRole.USER))
            .build())).collect(Collectors.toList());
        accountJpaRepository.saveAll(tempUsers);
        return accountQuerydslSupport.getTempUsersByUserNames(names);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AccountAdapter(accountJpaRepository.findByUsernameWithRoles(username)
                .orElseThrow(()->new UsernameNotFoundException(username)));
    }

    @Override
    public List getTempUsersByUserNames(List names) {
        //id가 temp_이름  인 사람들
        return accountQuerydslSupport.getTempUsersByUserNames(names);
    }

}
