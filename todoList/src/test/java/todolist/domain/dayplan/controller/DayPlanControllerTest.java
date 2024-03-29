package todolist.domain.dayplan.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import todolist.domain.dayplan.dto.apidto.request.DayPlanCreateApiDto;
import todolist.domain.dayplan.dto.apidto.request.DayPlanUpdateApiDto;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanUpdateServiceDto;
import todolist.global.testHelper.ControllerTest;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static todolist.auth.utils.AuthConstant.AUTHORIZATION;

class DayPlanControllerTest extends ControllerTest {

    @Override
    public String getUrl() {
        return "/v1/api/dayplans";
    }

    @Test
    @DisplayName("dayPlan 조회 api")
    void getDayPlans() throws Exception {

        //given
        //mock 응답값 생성
        List<DayPlanResponseServiceDto> serviceDto =
                createDayPlanResponseServiceDtos(LocalDate.of(2023, 3, 1), 10);

        given(dayPlanService.findDayPlanList(anyLong(), any(LocalDate.class), any(LocalDate.class)))
            .willReturn(serviceDto);

        //비교할 응답값
        DayPlanListResponseApiDto responseDto = DayPlanListResponseApiDto.of(serviceDto);
        String content = objectMapper.writeValueAsString(ApiResponse.ok(responseDto));

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        LocalDate from = LocalDate.of(2023, 3, 1);
        LocalDate to = LocalDate.of(2023, 3, 10);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", from.toString());
        params.add("to", to.toString());

        //when
        ResultActions actions = mockMvc.perform(getBuilder(withDefaultUrl(), params)
                .header(AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //restdocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        queryParameters(
                                parameterWithName("from").description("검색 시작 날짜"),
                                parameterWithName("to").description("검색 종료 날짜")
                        ),
                        getListResponseFields(
                                fieldWithPath("data.dayPlans").type(ARRAY).description("일정 목록"),
                                fieldWithPath("data.dayPlans[].id").type(NUMBER).description("일정 id"),
                                fieldWithPath("data.dayPlans[].content").type(STRING).description("일정 내용"),
                                fieldWithPath("data.dayPlans[].date").type(STRING).description("일정 날짜"),
                                fieldWithPath("data.dayPlans[].startTime").type(STRING).description("일정 시작 시간"),
                                fieldWithPath("data.dayPlans[].endTime").type(STRING).description("일정 종료 시간"),
                                fieldWithPath("data.dayPlans[].done").type(BOOLEAN).description("일정 완료 여부"),
                                fieldWithPath("data.dayPlans[].todoId").type(NUMBER).description("일정에 연결된 todo id"),
                                fieldWithPath("data.dayPlans[].categoryId").type(NUMBER).description("일정의 카테고리")
                        )
                ));
    }



    @Test
    @DisplayName("dayPlan 생성 api")
    void createDayPlan() throws Exception {
        //given
        //생성 api request dto
        DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .todoId(1L)
                .categoryId(1L)
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        given(dayPlanService.saveDayPlan(anyLong(), any(DayPlanCreateServiceDto.class)))
                .willReturn(DayPlanResponseServiceDto.builder().build());

        //when
        ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                .header(AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated());

        //restdocs
        setConstraintClass(DayPlanCreateApiDto.class);

        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("일정 내용")
                                        .attributes(getConstraint("content")),
                                fieldWithPath("date").type(STRING).description("일정 날짜")
                                        .attributes(getConstraint("date")),
                                fieldWithPath("startTime").type(STRING).description("일정 시작 시간")
                                        .attributes(getConstraint("startTime")),
                                fieldWithPath("endTime").type(STRING).description("일정 종료 시간")
                                        .attributes(getConstraint("endTime")),
                                fieldWithPath("todoId").type(NUMBER).description("일정에 연결된 todo id").optional()
                                        .attributes(getConstraint("todoId")),
                                fieldWithPath("categoryId").type(NUMBER).description("일정의 카테고리")
                                        .attributes(getConstraint("categoryId"))
                        )
                ));

    }

    @Test
    @DisplayName("dayPlan 수정 api")
    void updateDayPlan() throws Exception {
        //given
        //업데이트 api request dto
        DayPlanUpdateApiDto dto = DayPlanUpdateApiDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .isDone(true)
                .todoId(1L)
                .categoryId(1L)
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(dayPlanService).updateDayPlan(anyLong(), anyLong(), any(DayPlanUpdateServiceDto.class));

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/{dayPlanId}", content, 1)
                .header(AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        setConstraintClass(DayPlanUpdateApiDto.class);

        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        pathParameters(
                                parameterWithName("dayPlanId").description("수정할 dayPlan id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("일정 내용").optional()
                                        .attributes(getConstraint("content")),
                                fieldWithPath("date").type(STRING).description("일정 날짜").optional(),
                                fieldWithPath("startTime").type(STRING).description("일정 시작 시간").optional(),
                                fieldWithPath("endTime").type(STRING).description("일정 종료 시간").optional(),
                                fieldWithPath("isDone").type(BOOLEAN).description("일정 종료 시간").optional(),
                                fieldWithPath("todoId").type(NUMBER).description("일정에 연결할 todo id").optional(),
                                fieldWithPath("deleteTodo").type(BOOLEAN).description("false 를 주면 todo 와 연결이 끊김").optional(),
                                fieldWithPath("categoryId").type(NUMBER).description("일정의 카테고리").optional()
                        )
                ));
    }

    @Test
    @DisplayName("dayPlan 삭제 api")
    void deleteDayPlan() throws Exception {
        //given
        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(dayPlanService).deleteDayPlan(anyLong(), anyLong());

        //when
        ResultActions actions = mockMvc.perform(deleteBuilder("/{dayPlanId}", 1)
                .header(AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        actions
                .andDo(documentHandler.document(
                        getTokenRequestHeader(),
                        pathParameters(
                                parameterWithName("dayPlanId").description("수정할 dayPlan id")
                        )
                ));

    }

    @TestFactory
    @DisplayName("dayPlan 생성 시 validation 검증")
    Collection<DynamicTest> createDayPlanValidation() {
        //given
        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        given(dayPlanService.saveDayPlan(anyLong(), any(DayPlanCreateServiceDto.class)))
                .willReturn(DayPlanResponseServiceDto.builder().build());

        return List.of(
                dynamicTest("content 가 공백문자일 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content(" ")
                            .date(LocalDate.of(2023, 7, 20))
                            .startTime(LocalTime.of(12, 0, 0))
                            .endTime(LocalTime.of(12, 20, 0))
                            .categoryId(1L)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("내용을 입력해주세요."));
                }),
                dynamicTest("content 의 길이가 100자가 넘을 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content("a".repeat(101))
                            .date(LocalDate.of(2023, 7, 20))
                            .startTime(LocalTime.of(12, 0, 0))
                            .endTime(LocalTime.of(12, 20, 0))
                            .categoryId(1L)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("가능한 길이는 1 ~ 100자 입니다."));
                }),
                dynamicTest("date 가 null 일 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content("content")
                            .date(null)
                            .startTime(LocalTime.of(12, 0, 0))
                            .endTime(LocalTime.of(12, 20, 0))
                            .categoryId(1L)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("날짜를 입력해주세요."));
                }),
                dynamicTest("startTime 이 null 일 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content("content")
                            .date(LocalDate.of(2023, 7, 20))
                            .startTime(null)
                            .endTime(LocalTime.of(12, 20, 0))
                            .categoryId(1L)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("시간을 입력해주세요."));
                }),
                dynamicTest("endTime 이 null 일 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content("content")
                            .date(LocalDate.of(2023, 7, 20))
                            .startTime(LocalTime.of(12, 0, 0))
                            .endTime(null)
                            .categoryId(1L)
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("시간을 입력해주세요."));
                }),
                dynamicTest("categoryId 가 null 일 때", () ->{
                    //given
                    //생성 api request dto
                    DayPlanCreateApiDto dto = DayPlanCreateApiDto.builder()
                            .content("content")
                            .date(LocalDate.of(2023, 7, 20))
                            .startTime(LocalTime.of(12, 0, 0))
                            .endTime(LocalTime.of(12, 10, 0))
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content)
                            .header(AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("카테고리를 선택해주세요."));
                })
        );
    }

    @Test
    @DisplayName("dayPlan 수정 시 validation 검증 - content 의 길이가 100자가 넘을 때")
    void updateDayPlanValidation() throws Exception {
        //given
        //업데이트 api request dto
        DayPlanUpdateApiDto dto = DayPlanUpdateApiDto.builder()
                .content("a".repeat(101))
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .isDone(true)
                .todoId(1L)
                .categoryId(1L)
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증값 설정
        long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(dayPlanService).updateDayPlan(anyLong(), anyLong(), any(DayPlanUpdateServiceDto.class));

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/{dayPlanId}", content, 1)
                .header(AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].reason").value("가능한 길이는 1 ~ 100자 입니다."));
    }

    List<DayPlanResponseServiceDto> createDayPlanResponseServiceDtos(LocalDate startDate, int count){
        List<DayPlanResponseServiceDto> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            dtos.add(createDayPlanResponseServiceDto((long) i, startDate.plusDays(i - 1)));
        }
        return dtos;
    }

    DayPlanResponseServiceDto createDayPlanResponseServiceDto(Long dayPlanId, LocalDate date){
        return DayPlanResponseServiceDto.builder()
                .id(dayPlanId)
                .content("test")
                .date(date)
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .isDone(false)
                .todoId(dayPlanId) //todoId 와 dayPlanId 은 테스트 상 같게 설정
                .categoryId(dayPlanId) //categoryId 와 dayPlanId 은 테스트 상 같게 설정
                .build();

    }
}