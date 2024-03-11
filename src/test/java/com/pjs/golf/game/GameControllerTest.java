package com.pjs.golf.game;

import com.pjs.golf.account.dto.AccountDto;
import com.pjs.golf.account.entity.AccountRole;
import com.pjs.golf.account.entity.Gender;
import com.pjs.golf.account.service.AccountMapper;
import com.pjs.golf.account.service.AccountService;
import com.pjs.golf.common.BaseControllerTest;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.fields.service.FieldsService;
import com.pjs.golf.game.dto.GameDto;
import com.pjs.golf.game.service.GameService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends BaseControllerTest {

    @Autowired
    GameService gameService;

    @Autowired
    AccountService accountService;


    @Autowired
    FieldsService fieldsService;


    private String getBaererToken(int i) throws Exception {
        return "Bearer " + getAccescToken(i);
    }
    private String getAccescToken(int i) throws Exception {

        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "9301234569"+i;
        String password = "1234";
        LocalDateTime joninDate = LocalDateTime.now();
        String name = "이름" + i;
        AccountDto testUser = AccountDto.builder()
                .username(username)
                .password(password)
                .name(name)
                .birth("6001011")
                .gender(Gender.MALE)
                .joinDate(joninDate)
                .roles(Set.of(AccountRole.USER))
                .build();
        this.accountService.saveAccount(AccountMapper.Instance.toEntity(testUser));
        String Token = this.accountService.authorize(testUser, response, request);
        return Token;
    }

    private String getToken(int i) {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "9301234569"+i;
        String password = "1234";
        LocalDateTime joninDate = LocalDateTime.now();
        String name = "이름" + i;
        AccountDto testUser = AccountDto.builder()
                .username(username)
                .password(password)
                .name(name)
                .birth("6001011")
                .gender(Gender.MALE)
                .joinDate(joninDate)
                .roles(Set.of(AccountRole.USER))
                .build();
        return  this.accountService.authorize(testUser, response, request);
    }

    @Test
    @Order(1)
    @Description("[정상]등록 테스트")
    public void createTest()throws Exception {
        Fields fields = Fields.builder()
                .holes(9)
                .createDate(LocalDateTime.now())
                .name("경기장")
                .address("경기 주소")
                .build();

        Fields field = fieldsService.createField(fields);
        GameDto game = GameDto.builder()
                .title("제목이 매우 길어서 20자를 넘으면 어떻게 될지에 대해서 궁금해서 시도해 보았습니다.")
                .fields(field)
                .detail("경기 상세")
                .playDate(LocalDateTime.of(2023,10,15,14,30))
                .build();
        mockMvc.perform(post("/api/game")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(game)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("game-create-api",
                        preprocessRequest(
                                Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("sejong-parkgolf.com")
                                        .removePort()

                        ),
                        links(
                                linkWithRel("self").description("자기 자신의 링크"),
                                linkWithRel("update").description("수정 링크"),
                                linkWithRel("profile").description("프로필")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON TYPE")
                        ),
                        relaxedResponseFields(
                                //                          responseFields(
                                fieldWithPath("id").description("등록 경기 식별자"),
                                fieldWithPath("title").description("경기 제목"),
                                fieldWithPath("fields").description("경기장 정보"),
                                fieldWithPath("detail").description("경기 상세 내용"),
                                fieldWithPath("createDate").description("경기 등록 일자"),
                                fieldWithPath("playDate").description("경기 일자"),
                                fieldWithPath("_links.self.href").description("상세페이지 링크"),
                                fieldWithPath("_links.update.href").description("수정페이지 링크"),
                                fieldWithPath("_links.profile.href").description("프로필")
                        )

                ));
    }



    @Test
    @Order(2)
    @Description("[정상]리스트 조회 테스트")
    public void queryListTest()throws Exception{


        mockMvc.perform(get("/api/game/")
                    .param("startDate", "2023-01-01T00:00:00")
                    .param("endDate", "2023-03-01T00:00:00")
                    .param("searchTxt", "대교리")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("game-list-api",
                        preprocessRequest(
                                Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("sejong-parkgolf.com")
                                        .removePort()
                        ),
                        requestParameters(
                                parameterWithName("startDate").optional().description("검색 시작일"),
                                parameterWithName("endDate").optional().description("검색 종료일"),
                                parameterWithName("searchTxt").optional().description("검색어"),
                                parameterWithName("page").optional().description("페이지 번호"),
                                parameterWithName("size").optional().description("페이지 사이즈")
                        ),
                        links(
                                linkWithRel("profile").description("프로필"),
                                linkWithRel("self").description("현재 링크")
                        )
                ));
    }

    @Test
    @Description("상세조회 [유저 O]테스트")
    public void queryViewTest()throws Exception{
        mockMvc.perform(get("/api/game/{id}",1)
                        .header(HttpHeaders.AUTHORIZATION, getToken(1))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andDo(document("game-view-api",
                        preprocessRequest(
                                Preprocessors.modifyUris()
                                        .scheme("https")
                                        .host("sejong-parkgolf.com")
                                        .removePort()
                        ),
                        requestParameters(
                                parameterWithName("id").optional().description("조회할 경기의 아이디")
                        ),
                        links(
                                linkWithRel("profile").description("프로필"),
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("update").description("수정 링크")
                        )
                ));
    }


    @Test
    @Description("상세조회 [유저 X] 테스트")
    public void queryViewAnonymousTest()throws Exception{
        mockMvc.perform(get("/api/game/{id}",1)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update").doesNotExist());
    }



}