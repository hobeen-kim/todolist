package todolist.domain.member.controller;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import todolist.auth.utils.AuthConstant;
import todolist.domain.member.dto.apidto.request.MemberAuthorityApiDto;
import todolist.domain.member.dto.apidto.request.MemberCreateApiDto;
import todolist.domain.member.dto.apidto.request.MemberPasswordApiDto;
import todolist.domain.member.dto.apidto.request.MemberWithdrawalApiDto;
import todolist.domain.member.dto.apidto.response.MemberPageResponseApiDto;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.domain.member.entity.Authority;
import todolist.global.ControllerTest;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberControllerTest extends ControllerTest {

    @Override
    public String getUrl() {
        return "/v1/api/members";
    }

    @Test
    @DisplayName("member 회원 가입 api")
    void signUp() throws Exception{
        //given
        //요청 값 생성
        MemberCreateApiDto dto = MemberCreateApiDto.builder()
                .name("name")
                .username("username")
                .password("1234")
                .email("test@test.com")
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //mock 응답값 생성
        Long mockMemberId = 1L;

        given(memberService.saveMember(any(MemberCreateServiceDto.class)))
                .willReturn(mockMemberId);

        //when
        ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/api/members/" + mockMemberId));

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                            requestFields(
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("username").type(STRING).description("회원 아이디"),
                                fieldWithPath("password").type(STRING).description("회원 비밀번호"),
                                fieldWithPath("email").type(STRING).description("회원 이메일")
                            ),
                            responseHeaders(
                                headerWithName("Location").description("생성된 회원의 id")
                            )
                        )
                );
    }

    @Test
    @DisplayName("member 정보 요청 api")
    void getMember() throws Exception {
        //given
        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        MemberResponseServiceDto serviceDto = createResponseServiceDto(memberId);

        given(memberService.findMember(anyLong()))
                .willReturn(serviceDto);

        //기대값 생성
        String content = objectMapper.writeValueAsString(ApiResponse.ok(serviceDto));

        //when
        ResultActions actions = mockMvc.perform(getBuilder("/my-info")
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                                getTokenRequestHeader(),
                                getSingleResponseFields(
                                        fieldWithPath("data.id").type(NUMBER).description("회원 id"),
                                        fieldWithPath("data.name").type(STRING).description("회원 이름"),
                                        fieldWithPath("data.username").type(STRING).description("회원 아이디"),
                                        fieldWithPath("data.email").type(STRING).description("회원 이메일"),
                                        fieldWithPath("data.authority").type(STRING).description("회원 권한"),
                                        fieldWithPath("data.createdDate").type(STRING).description("회원 생성일")
                                )
                        )
                );
    }

    @Test
    @DisplayName("member password 변경")
    void updatePassword() throws Exception {
        //given
        //요청 값 생성
        MemberPasswordApiDto dto = MemberPasswordApiDto.builder()
                .prevPassword("1234")
                .newPassword("12345")
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).changePassword(anyLong(), any(String.class), any(String.class));

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/password", content)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                                getTokenRequestHeader(),
                                requestFields(
                                        fieldWithPath("prevPassword").type(STRING).description("이전 비밀번호"),
                                        fieldWithPath("newPassword").type(STRING).description("새로운 비밀번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("member 리스트 조회 api (admin)")
    void getMemberList() throws Exception{
        //given
        //요청값 생성
        int page = 0;
        int size = 10;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));

        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponseServiceDto> pageResponseServiceDto = createPageResponseServiceDto(10, pageable);
        given(memberService.findMemberList(any(Pageable.class)))
                .willReturn(pageResponseServiceDto);

        //기대값 생성
        String content = objectMapper.writeValueAsString(ApiResponse.ok(MemberPageResponseApiDto.of(pageResponseServiceDto)));

        //when
        ResultActions actions = mockMvc.perform(getBuilder(withDefaultUrl(), params)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                                getTokenRequestHeader(),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈")
                                ),
                                getPageResponseFields(
                                        fieldWithPath("data.members[]").type(ARRAY).description("회원 리스트"),
                                        fieldWithPath("data.members[].id").type(NUMBER).description("회원 id"),
                                        fieldWithPath("data.members[].name").type(STRING).description("회원 이름"),
                                        fieldWithPath("data.members[].username").type(STRING).description("회원 아이디"),
                                        fieldWithPath("data.members[].email").type(STRING).description("회원 이메일"),
                                        fieldWithPath("data.members[].authority").type(STRING).description("회원 권한"),
                                        fieldWithPath("data.members[].createdDate").type(STRING).description("회원 생성일")
                                )

                        )
                );
    }

    @Test
    @DisplayName("권한 변경 api (admin)")
    void updateAuthority() throws Exception{
        //given
        //요청값 생성
        MemberAuthorityApiDto dto = MemberAuthorityApiDto.builder()
                .authority(Authority.ROLE_ADMIN)
                .build();

        Long changeId = 2L;

        String content = objectMapper.writeValueAsString(dto);

        //인증 값 설정 (admin)
        Long memberId = 1L;
        setAdminAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).changeAuthority(anyLong(), anyLong(), any(Authority.class));

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/authority/{memberId}", content, changeId)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                                getTokenRequestHeader(),
                                pathParameters(
                                        parameterWithName("memberId").description("변경할 회원 id")
                                ),
                                requestFields(
                                        fieldWithPath("authority").type(STRING).description("변경할 권한")
                                )

                        )
                );
    }

    @Test
    @DisplayName("회원 삭제 api")
    void withdrawal() throws Exception {
        //given
        //요청값 생성
        MemberWithdrawalApiDto dto = MemberWithdrawalApiDto.builder()
                .password("1234")
                .build();

        String content = objectMapper.writeValueAsString(dto);

        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).withdrawal(anyLong(), anyLong(), any(String.class));

        //when
        ResultActions actions = mockMvc.perform(deleteBuilder("/{memberId}", content, memberId)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        actions
                .andDo(
                        documentHandler.document(
                                getTokenRequestHeader(),
                                pathParameters(
                                        parameterWithName("memberId").description("삭제할 회원 id")
                                ),
                                requestFields(
                                        fieldWithPath("password").type(STRING).description("회원 비밀번호")
                                )
                        )
                );

    }

    @TestFactory
    @DisplayName("회원 가입 시 validation 검증")
    Collection<DynamicTest> memberCreateValidation() {
        //given
        //mock 응답값 생성
        Long mockMemberId = 1L;

        given(memberService.saveMember(any(MemberCreateServiceDto.class)))
                .willReturn(mockMemberId);

        return List.of(
                dynamicTest("이름이 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberCreateApiDto dto = MemberCreateApiDto.builder()
                            .name(" ")
                            .username("username")
                            .password("1234")
                            .email("test@test.com")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("이름을 입력해주세요."));
                }),
                dynamicTest("아이디가 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberCreateApiDto dto = MemberCreateApiDto.builder()
                            .name("name")
                            .username(" ")
                            .password("1234")
                            .email("test@test.com")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
                }),
                dynamicTest("비밀번호가 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberCreateApiDto dto = MemberCreateApiDto.builder()
                            .name("name")
                            .username("username")
                            .email("test@test.com")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."));
                }),
                dynamicTest("이메일이 이메일 형식이 아닐 때", () ->{
                    //given
                    //요청 값 생성
                    MemberCreateApiDto dto = MemberCreateApiDto.builder()
                            .name("name")
                            .username("username")
                            .password("1234")
                            .email("test.com")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("이메일을 정확히 입력해주세요."));
                }),
                dynamicTest("이메일이 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberCreateApiDto dto = MemberCreateApiDto.builder()
                            .name("name")
                            .username("username")
                            .password("1234")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(postBuilder(withDefaultUrl(), content));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("이메일을 정확히 입력해주세요."));
                })
        );

    }

    @TestFactory
    @DisplayName("회원 패스워드 수정 시 validation 검증")
    Collection<DynamicTest> updatePasswordValidation() throws Exception {
        //given
        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).changePassword(anyLong(), any(String.class), any(String.class));

        return List.of(
                dynamicTest("이전 비밀번호가 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberPasswordApiDto dto = MemberPasswordApiDto.builder()
                            .prevPassword(" ")
                            .newPassword("12345")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(patchBuilder("/password", content)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."));
                }),
                dynamicTest("이전 비밀번호가 null 일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberPasswordApiDto dto = MemberPasswordApiDto.builder()
                            .newPassword("12345")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(patchBuilder("/password", content)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."));
                }),
                dynamicTest("새 비밀번호가 공백일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberPasswordApiDto dto = MemberPasswordApiDto.builder()
                            .prevPassword("1234")
                            .newPassword(" ")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(patchBuilder("/password", content)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."));
                }),
                dynamicTest("새 비밀번호가 null 일 때", () ->{
                    //given
                    //요청 값 생성
                    MemberPasswordApiDto dto = MemberPasswordApiDto.builder()
                            .prevPassword("1234")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(patchBuilder("/password", content)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."));
                })
        );
    }


    @TestFactory
    @DisplayName("회원 탈퇴 시 validation 검증")
    Collection<DynamicTest> withdrawalValidation() {
        //given
        //인증 값 설정
        Long memberId = 1L;
        setDefaultAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).withdrawal(anyLong(), anyLong(), any(String.class));


        return List.of(
                dynamicTest("비밀번호가 공백일 때", () -> {
                    //given
                    //요청 값 생성
                    MemberWithdrawalApiDto dto = MemberWithdrawalApiDto.builder()
                            .password(" ")
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(deleteBuilder("/{memberId}", content, memberId)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."))
                    ;
                }),
                dynamicTest("비밀번호가 null 일 때", () -> {
                    //given
                    //요청 값 생성
                    MemberWithdrawalApiDto dto = MemberWithdrawalApiDto.builder()
                            .build();

                    String content = objectMapper.writeValueAsString(dto);

                    //when
                    ResultActions actions = mockMvc.perform(deleteBuilder("/{memberId}", content, memberId)
                            .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("비밀번호를 정확히 입력해주세요."))
                    ;
                })

            );

    }

    @Test
    @DisplayName("회원 권한 수정 시 validation 검증")
    void updateAuthorityValidation() throws Exception {
        //given
        //인증 값 설정 (admin)
        Long memberId = 1L;
        setAdminAuthentication(memberId);

        //mock 응답값 생성
        willDoNothing().given(memberService).changeAuthority(anyLong(), anyLong(), any(Authority.class));

        //요청 값 생성
        MemberAuthorityApiDto dto = MemberAuthorityApiDto.builder()
                .build();

        Long changeId = 2L;

        String content = objectMapper.writeValueAsString(dto);

        //when
        ResultActions actions = mockMvc.perform(patchBuilder("/authority/{memberId}", content, changeId)
                .header(AuthConstant.AUTHORIZATION, getAuthorizationToken()));


        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("권한을 선택해주세요."))
        ;
    }


    MemberResponseServiceDto createResponseServiceDto(Long memberId){
        return MemberResponseServiceDto.builder()
                .id(memberId)
                .name("name " + memberId)
                .username("username " + memberId)
                .email("test@test.com")
                .authority(Authority.ROLE_USER)
                .createdDate(LocalDateTime.of(2023, 7, 20, 12, 0, 0))
                .build();
    }

    Page<MemberResponseServiceDto> createPageResponseServiceDto(int count, Pageable pageable){

        List<MemberResponseServiceDto> content = new ArrayList<>();

        for(int i = 1; i <= count; i++){
            content.add(createResponseServiceDto((long) i));
        }

        return new PageImpl<>(content, pageable, count);
    }

}