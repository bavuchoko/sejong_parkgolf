package com.pjs.golf.account.service;

import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService extends UserDetailsService {
    Account saveAccount(Account account);

    String authorize(AccountDto accountDto, HttpServletResponse response, HttpServletRequest request);

    String reIssueToken(HttpServletRequest request);

    void logout(HttpServletRequest req);

    Page<Account> loadUserList(Pageable pagable);

    Map getResponseMap(String accessToken);

    boolean validateToken(String token);

    List createUserIfDosenExist(List<String> name);


    List getTempUsersByUserNames(List names);

}
