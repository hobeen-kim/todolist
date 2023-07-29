package todolist.domain.todo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import todolist.auth.service.TokenProvider;
import todolist.auth.utils.AuthConstant;
import todolist.domain.dayplan.dto.apidto.request.DayPlanCreateApiDto;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.todo.dto.apidto.request.TodoCreateApiDto;
import todolist.domain.todo.dto.apidto.request.TodoUpdateApiDto;
import todolist.domain.todo.dto.apidto.response.TodoListResponseApiDto;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoUpdateServiceDto;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.global.ControllerTest;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static todolist.auth.utils.AuthConstant.AUTHORIZATION;

class TodoControllerTest extends ControllerTest {

    @Override
    public String getUrl() {
        return "/v1/api/todos";
    }

    @Test
    @DisplayName("todo 조회 api")
    void getTodos() throws Exception {

        //given
        //mock 데이터 생성
        List<TodoResponseServiceDto> serviceDto =
                createTodoResponseServiceDtos(LocalDate.of(2023, 3, 1), 10);

        given(todoService.findTodoList(anyLong(), any(LocalDate.class), any(LocalDate.class), any(SearchType.class)))
                .willReturn(serviceDto);


        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //요청값 설정
        LocalDate from = LocalDate.of(2023, 3, 1);
        LocalDate to = LocalDate.of(2023, 3, 10);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", from.toString());
        params.add("to", to.toString());
        params.add("searchType", "START_DATE");

        //비교할 응답값
        TodoListResponseApiDto responseDto = TodoListResponseApiDto.of(serviceDto);
        String content = objectMapper.writeValueAsString(ApiResponse.ok(responseDto));

        //when
        ResultActions actions = mockMvc.perform(getBuilder(withDefaultUrl(), params)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content))
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
                                fieldWithPath("data.todos").type(ARRAY).description("todo 목록"),
                                fieldWithPath("data.todos[].id").type(NUMBER).description("todo id"),
                                fieldWithPath("data.todos[].importance").type(STRING).description("todo 중요도"),
                                fieldWithPath("data.todos[].content").type(STRING).description("todo 내용"),
                                fieldWithPath("data.todos[].startDate").type(STRING).description("todo 시작 예정일"),
                                fieldWithPath("data.todos[].deadLine").type(STRING).description("todo 마감 예정일"),
                                fieldWithPath("data.todos[].doneDate").type(STRING).description("todo 완료일"),
                                fieldWithPath("data.todos[].done").type(BOOLEAN).description("todo 완료 여부")
                        )
                ));
    }

    @Test
    @DisplayName("todo 생성 api")
    void createTodo() throws Exception {
        //given
        //생성 api request dto
        TodoCreateApiDto dto = TodoCreateApiDto.builder()
                .content("content")
                .importance(Importance.BLUE)
                .startDate(LocalDate.of(2023, 3, 1))
                .deadLine(LocalDate.of(2023, 3, 10))
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 데이터 생성
        given(todoService.saveTodo(anyLong(), any(TodoCreateServiceDto.class)))
                .willReturn(TodoResponseServiceDto.builder().build());

        //when
        ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        //restDocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("todo 내용"),
                                fieldWithPath("importance").type(STRING).description("todo 중요도"),
                                fieldWithPath("startDate").type(STRING).description("todo 시작 예정일"),
                                fieldWithPath("deadLine").type(STRING).description("todo 마감 예정일")
                        )
                ));
    }

    @Test
    @DisplayName("todo 수정 api")
    void updateTodo() throws Exception{

        //given
        //수정 api request dto
        TodoUpdateApiDto dto = TodoUpdateApiDto.builder()
                .content("content")
                .importance(Importance.BLUE)
                .startDate(LocalDate.of(2023, 3, 1))
                .deadLine(LocalDate.of(2023, 3, 10))
                .doneDate(LocalDate.of(2023, 3, 5))
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 데이터 생성
        willDoNothing().given(todoService).updateTodo(anyLong(), any(TodoUpdateServiceDto.class));

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/{todoId}", content, 1L)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restDocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        pathParameters(
                                parameterWithName("todoId").description("수정할 todo id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("todo 내용"),
                                fieldWithPath("importance").type(STRING).description("todo 중요도"),
                                fieldWithPath("startDate").type(STRING).description("todo 시작 예정일"),
                                fieldWithPath("deadLine").type(STRING).description("todo 마감 예정일"),
                                fieldWithPath("doneDate").type(STRING).description("todo 완료일")
                        )
                ));


    }

    @Test
    @DisplayName("todo 삭제 api")
    void deleteTodo() throws Exception {

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 데이터 생성
        willDoNothing().given(todoService).deleteTodo(anyLong(), anyLong());

        //when
        ResultActions actions = mockMvc.perform(deleteBuilder("/{todoId}", 1L)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restDocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        pathParameters(
                                parameterWithName("todoId").description("삭제할 todo id")
                        )
                ));

    }

    @TestFactory
    @DisplayName("todo 생성 시 validation 검증")
    Collection<DynamicTest> createTodoValidation() {
        //given
        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        given(todoService.saveTodo(anyLong(), any(TodoCreateServiceDto.class)))
                .willReturn(TodoResponseServiceDto.builder().build());

        return List.of(
                dynamicTest("content 가 공백문자일 때 예외가 발생한다.", () ->{
                    //given
                    //생성 api request dto
                    TodoCreateApiDto dto = TodoCreateApiDto.builder()
                            .content(" ")
                            .importance(Importance.BLUE)
                            .startDate(LocalDate.of(2023, 3, 1))
                            .deadLine(LocalDate.of(2023, 3, 10))
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("내용을 입력해주세요."));
                }),
                dynamicTest("importance 가 null 일 때 예외가 발생한다.", () ->{
                    //given
                    //생성 api request dto
                    TodoCreateApiDto dto = TodoCreateApiDto.builder()
                            .content("content")
                            .importance(null)
                            .startDate(LocalDate.of(2023, 3, 1))
                            .deadLine(LocalDate.of(2023, 3, 10))
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("중요도를 입력해주세요."));
                }),
                dynamicTest("startDate 가 null 일 때 예외가 발생한다.", () ->{
                    //given
                    //생성 api request dto
                    TodoCreateApiDto dto = TodoCreateApiDto.builder()
                            .content("content")
                            .importance(Importance.BLUE)
                            .startDate(null)
                            .deadLine(LocalDate.of(2023, 3, 10))
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("날짜를 입력해주세요."));
                }),
                dynamicTest("deadLine 이 null 일 때 예외가 발생한다.", () ->{
                    //given
                    //생성 api request dto
                    TodoCreateApiDto dto = TodoCreateApiDto.builder()
                            .content("content")
                            .importance(Importance.BLUE)
                            .startDate(LocalDate.of(2023, 3, 1))
                            .deadLine(null)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("날짜를 입력해주세요."));
                })
        );
    }

    @Test
    @DisplayName("todo 조회 시 validation 검증 - searchType 이 없을 때 예외가 발생한다.")
    void getTodoListValidation() throws Exception {
        //given
        //mock 데이터 생성
        given(todoService.findTodoList(anyLong(), any(LocalDate.class), any(LocalDate.class), any(SearchType.class)))
                .willReturn(new ArrayList<>());

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //요청값 설정
        LocalDate from = LocalDate.of(2023, 3, 1);
        LocalDate to = LocalDate.of(2023, 3, 10);
        //요청 파라미터에 searchType 이 없을 때
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", from.toString());
        params.add("to", to.toString());

        //when
        ResultActions actions = mockMvc.perform(getBuilder(withDefaultUrl(), params)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("검색조건을 선택해주세요."))
        ;
    }

    List<TodoResponseServiceDto> createTodoResponseServiceDtos(LocalDate startDate, int count){
        List<TodoResponseServiceDto> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            dtos.add(createTodoResponseServiceDto((long) i, startDate.plusDays(i - 1)));
        }
        return dtos;
    }

    TodoResponseServiceDto createTodoResponseServiceDto(Long todoId, LocalDate startDate){
        return TodoResponseServiceDto.builder()
                .id(todoId)
                .content("content")
                .importance(Importance.RED)
                .startDate(startDate)
                .deadLine(startDate.plusDays(2))
                .doneDate(startDate.plusDays(1))
                .isDone(true)
                .build();

    }
}