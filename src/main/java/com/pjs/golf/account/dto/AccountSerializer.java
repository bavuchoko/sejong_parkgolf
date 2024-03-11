package com.pjs.golf.account.dto;

import com.pjs.golf.account.entity.Account;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("username", value.getUsername());
        jsonGenerator.writeStringField("joinDate", value.getJoinDate().toString());
        jsonGenerator.writeStringField("name", value.getName());
        jsonGenerator.writeStringField("birth", value.getBirth());
        jsonGenerator.writeStringField("portrait", value.getPortrait());
        jsonGenerator.writeStringField("gender", value.getGender().toString());
        jsonGenerator.writeEndObject();
    }
}
