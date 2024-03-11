package com.pjs.golf.account.service;

import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.Account;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountMapper Instance = Mappers.getMapper(AccountMapper.class);
    @Named("toEntity")
    Account toEntity(AccountDto accountDto);

    @Mapping(target = "password", ignore = true)
    @Named("toDto")
    AccountDto toDto(Account account);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Named("withoutRoles")
    Account withoutRoles(Account account);

    @Named("entiTyToEntity")
    Account entiTyToEntity(Account account);




}
