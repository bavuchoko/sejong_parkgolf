package com.pjs.golf.account.dto;


import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account) {
        super(account.getUsername(), account.getPassword(), authorities(account.getRoles()));
        this.account =account;
    }


    /**
     * Account 에 roles 의 FetchType.Lazy 일 경우 game컨트롤러의 createGame에서 LazyInitialized 예외 발생
     * */
    private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    public Account getAccount() {
        return account;
    }
}
