package todolist.domain.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import todolist.auth.utils.AuthConstant;
import todolist.global.ControllerTest;

import java.time.LocalDate;

import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoControllerTest extends ControllerTest {

    @Override
    public String getUrl() {
        return "v1/api/todo";
    }

    @Test
    void getTodos() throws Exception {

        LocalDate from = LocalDate.of(2023, 3, 1);
        LocalDate to = LocalDate.of(2023, 3, 10);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", from.toString());
        params.add("to", to.toString());
        params.add("searchType", "START_DATE");

        //when
        ResultActions actions = mockMvc.perform(getBuilder(withDefaultUrl(), params)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
        ;

        //restDocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        queryParameters(
                                parameterWithName("from").description("검색 시작 날짜"),
                                parameterWithName("to").description("검색 종료 날짜"),
                                parameterWithName("searchType").description("검색 타입")
                        ),
                        getListResponseFields(
                                fieldWithPath("data.dayPlans").type(ARRAY).description("일정 목록"),
                                fieldWithPath("data.dayPlans[].id").type(NUMBER).description("일정 id"),
                                fieldWithPath("data.dayPlans[].content").type(STRING).description("일정 내용"),
                                fieldWithPath("data.dayPlans[].date").type(STRING).description("일정 날짜"),
                                fieldWithPath("data.dayPlans[].startTime").type(STRING).description("일정 시작 시간"),
                                fieldWithPath("data.dayPlans[].endTime").type(STRING).description("일정 종료 시간"),
                                fieldWithPath("data.dayPlans[].done").type(BOOLEAN).description("일정 완료 여부"),
                                fieldWithPath("data.dayPlans[].todoId").type(NUMBER).description("일정에 연결된 todo id")
                        )
                ));



    }

    @Test
    void createTodo() {
    }

    @Test
    void updateTodo() {
    }

    @Test
    void deleteTodo() {
    }
}