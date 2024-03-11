package com.pjs.golf.account.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountDtoSerializer extends JsonSerializer<AccountDto> {

    @Override
    public void serialize(AccountDto value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("username", value.getUsername());
        jsonGenerator.writeStringField("joinDate", value.getJoinDate().toString());
        jsonGenerator.writeStringField("name", value.getName());
        jsonGenerator.writeStringField("birth", value.getBirth());
        jsonGenerator.writeStringField("portrait", value.getPortrait());
        jsonGenerator.writeEndObject();
    }
}
