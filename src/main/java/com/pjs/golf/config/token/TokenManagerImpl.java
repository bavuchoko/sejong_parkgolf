package com.pjs.golf.config.token;

import com.pjs.golf.account.dto.AccountAdapter;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.repository.AccountJpaRepository;
import com.pjs.golf.common.WebCommon;
import com.pjs.golf.config.utils.CookieUtil;
import com.pjs.golf.config.utils.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenManagerImpl implements TokenManager, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenManagerImpl.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    private Key key;

    @Autowired
    CookieUtil cookieUtil;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    AccountJpaRepository accountJpaRepository;

    @Autowired
    WebCommon webCommon;

    public TokenManagerImpl(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
//        2시간
        this.accessTokenValidityTime = tokenValidityInSeconds;
//        1주일
        this.refreshTokenValidityTime = tokenValidityInSeconds * 7 * 12;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.printf("secret");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String createToken(Authentication authentication, TokenType tokenType) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        int id = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getId();
        String name = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getName();
        String joinDate = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getJoinDate().toString();
        String gender = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getGender().toString();
        String birth = ((AccountAdapter)(authentication.getPrincipal())).getAccount().getBirth();
        long now = (new Date()).getTime();
        Date validity =  tokenType == TokenType.ACCESS_TOKEN ?  new Date(now + this.accessTokenValidityTime) : new Date(now + this.refreshTokenValidityTime);
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", id);
        payloads.put("username", authentication.getName());
        payloads.put(AUTHORITIES_KEY, authorities);
        payloads.put("joinDate", joinDate);
        payloads.put("birth", birth);
        payloads.put("name", name == null ? "익명" : name);
        payloads.put("gender",gender);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(payloads)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    @Override
    public Authentication getAuthenticationFromRefreshToken(HttpServletRequest request) {
        String refreshTokenInCookie = cookieUtil.getCookie(request, TokenType.REFRESH_TOKEN.getValue()).getValue();
        String clientIP = webCommon.getClientIp(request);
        if (validateToken(refreshTokenInCookie)) {
            String storedIP = redisUtil.getData(refreshTokenInCookie);
//            if(clientIP.equals(storedIP)){
                return getAuthentication(refreshTokenInCookie);
//            }
        }
        return null;
    }


    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        String username = claims.get("username", String.class);


        UserDetails userDetails=  new AccountAdapter(accountJpaRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)));

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public String getId(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id").toString();
    }
    public String getUsername(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username").toString();
    }

    public String getName(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("name").toString();
    }
    public String getGender(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("gender").toString();
    }

    public String getBirth(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("birth").toString();
    }
    @Override
    public String getJoinDate(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("joinDate").toString();
    }


    @Override
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }

    }



    @Override
    public void destroyTokens(HttpServletRequest request) {

    }

}