package com.pjs.golf.sse;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pjs.golf.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class SSEService {
    private final Map<Long, Map<String, SseEmitter>>  sseEmitters = new ConcurrentHashMap<>();
    public SseEmitter subscribe(Long gameId, @Nullable Account account) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter emitter = new SseEmitter();
        String userId = account != null? String.valueOf(account.getId()) : UUID.randomUUID().toString();
        // 2. 연결
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. emitter를 사용자에 할당
        Map<String, SseEmitter> userEmitters = sseEmitters.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>());
        userEmitters.put(userId, emitter);

        // 4. 저장
        sseEmitters.put(gameId, userEmitters);

        // 4. 연결 종료 처리
        emitter.onCompletion(() -> removeEmitter(gameId, userId));
        emitter.onTimeout(() -> removeEmitter(gameId, userId));
        emitter.onError((e) -> removeEmitter(gameId, userId));

        return emitter;
    }
    private void removeEmitter(Long gameId, String userId) {
        Map<String, SseEmitter> userEmitters = sseEmitters.get(gameId);
        if (userEmitters != null) {
            userEmitters.remove(userId);
            if (userEmitters.isEmpty()) {
                sseEmitters.remove(gameId);
            }
        }
    }
    public void broadCast(Long gameId, EntityModel entityModel) {

        // 5. 경기별 구독자들을 꺼냄
        Map<String, SseEmitter> userEmitter = sseEmitters.get(gameId);

        // 6. 업데이트 data 전송 및 해체
        if (userEmitter != null && !userEmitter.isEmpty()) {
            try {
                // 6. 해당 경기의 사용자들을 찾아서 send
                for (SseEmitter emitter : userEmitter.values()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    String jsonPayload = objectMapper.writeValueAsString(entityModel);
                    emitter.send(SseEmitter.event()
                            .name("broadCast")
                            .data(jsonPayload, MediaTypes.HAL_JSON));
                    String a="";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            sseEmitters.remove(gameId);
        }
    }

}
