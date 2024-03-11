package com.pjs.golf.account.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.AccountRole;
import com.pjs.golf.account.entity.Gender;
import com.pjs.golf.common.ModelMapperUtils;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = AccountDtoSerializer.class)
public class AccountDto {


    private Integer id;

    @NotBlank(message = "아이디는 필수값입니다.")
    private String username;

    @Pattern(regexp = "[0-9]{7}", message = "7자리의 숫자만 입력가능합니다")
    private String birth;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    private String name;
    private Gender gender;
    private String portrait;

    private Set<AccountRole> roles;
    private String token;
    private LocalDateTime joinDate;
//    public Account toEntity() {
//        return ModelMapperUtils.getModelMapper().map(this, Account.class);
//    }
}

