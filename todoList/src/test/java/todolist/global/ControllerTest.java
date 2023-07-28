package todolist.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import todolist.auth.service.CustomUserDetails;
import todolist.domain.dayplan.controller.DayPlanController;
import todolist.domain.dayplan.service.DayPlanService;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.member.service.MemberService;
import todolist.domain.todo.controller.TodoController;
import todolist.domain.todo.service.TodoService;
import org.springframework.restdocs.snippet.Attributes;
import todolist.global.reponse.ApiResponse;


import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static todolist.auth.utils.AuthConstant.AUTHORIZATION;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest({DayPlanController.class, TodoController.class}) //ocp 위반... 어떻게 해결하지?
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTest implements ControllerTestHelper{

    @MockBean protected DayPlanService dayPlanService;
    @MockBean protected TodoService todoService;
    @MockBean protected MemberService memberService;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected MockMvc mockMvc;

    protected RestDocumentationResultHandler documentHandler;

    @BeforeEach
    void setUp(WebApplicationContext context,
               final RestDocumentationContextProvider restDocumentation,
               TestInfo testInfo) {


        String className = testInfo.getTestClass().orElseThrow().getSimpleName()
                .replace("ControllerTest", "").toLowerCase();
        String methodName = testInfo.getTestMethod().orElseThrow().getName().toLowerCase();

        documentHandler = document(
                className + "/" + methodName,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );

        DefaultMockMvcBuilder mockMvcBuilder = webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .addFilters(new CharacterEncodingFilter("UTF-8", true));


        //validation 은 문서화하지 않음
        if(!methodName.contains("validation")){
            mockMvcBuilder.alwaysDo(documentHandler);
        }

        mockMvc = mockMvcBuilder.build();
    }

    protected static String generateLinkCode(Class<?> clazz) {
        return String.format("link:common/%s.html[Enum,role=\"popup\"]", clazz.getSimpleName().toLowerCase());
    }

    protected Attributes.Attribute getFormat(
            final String value){
        return new Attributes.Attribute("format",value);
    }

    protected RequestHeadersSnippet getTokenRequestHeader() {

        return requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token").attributes(getFormat(getAuthorizationToken()))
        );
    }

    protected RequestHeadersSnippet getTokenRequestHeader(HeaderDescriptor... descriptors) {

        RequestHeadersSnippet requestHeadersSnippet = requestHeaders(
                headerWithName(AUTHORIZATION).description("Access Token").attributes(getFormat(getAuthorizationToken()))
        );

        return requestHeadersSnippet.and(descriptors);
    }

    protected ResponseFieldsSnippet getSingleResponseFields(FieldDescriptor... descriptors) {
        ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                fieldWithPath("status").type(STRING).description("응답 상태"),
                fieldWithPath("message").type(STRING).description("응답 메세지"),
                fieldWithPath("data").type(OBJECT).description("응답 데이터")
        );


        return responseFieldsSnippet.and(descriptors);
    }

    protected ResponseFieldsSnippet getListResponseFields(FieldDescriptor... descriptors) {
        ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                fieldWithPath("status").type(STRING).description("응답 상태"),
                fieldWithPath("message").type(STRING).description("응답 메세지"),
                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                fieldWithPath("data.pageInfo").type(NULL).description("페이징 정보가 없습니다.")
        );

        return responseFieldsSnippet.and(descriptors);
    }

    protected ResponseFieldsSnippet getPageResponseFields(FieldDescriptor... descriptors) {
        ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                fieldWithPath("status").type(STRING).description("응답 상태"),
                fieldWithPath("message").type(STRING).description("응답 메세지"),
                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                fieldWithPath("data.pageInfo").type(OBJECT).description("페이징 정보"),
                fieldWithPath("data.pageInfo.page").type(NUMBER).description("현재 페이지"),
                fieldWithPath("data.pageInfo.size").type(NUMBER).description("현재 개수"),
                fieldWithPath("data.pageInfo.totalPage").type(NUMBER).description("총 페이지"),
                fieldWithPath("data.pageInfo.totalSize").type(NUMBER).description("총 개수"),
                fieldWithPath("data.pageInfo.first").type(BOOLEAN).description("첫 페이지인지"),
                fieldWithPath("data.pageInfo.last").type(BOOLEAN).description("마지막 페이지인지"),
                fieldWithPath("data.pageInfo.hasNext").type(BOOLEAN).description("다음 페이지 여부"),
                fieldWithPath("data.pageInfo.hasPrevious").type(BOOLEAN).description("이전 페이지 여부")
        );

        responseFieldsSnippet.and(descriptors);

        return responseFieldsSnippet;
    }

    /**
     * ResultActions 에서 ApiResponse 를 가져온다.
     * @param actions ResultActions
     * @param clazz ApiResponse 의 data 타입
     * @return ApiResponse
     */
    protected <T> ApiResponse<T> getApiResponseFromResult(ResultActions actions, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
        String contentAsString = actions.andReturn().getResponse().getContentAsString();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, clazz);

        return objectMapper.readValue(contentAsString, javaType);
    }

    protected Member createMemberDefault(){
        return Member.builder()
                .username("test")
                .password("1234")
                .email("test@test.com")
                .authority(Authority.ROLE_USER)
                .build();
    }

    protected void setDefaultAuthentication(Member member){
        UserDetails userDetails = createUserDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextImpl securityContext = new SecurityContextImpl(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    protected void setDefaultAuthentication(Long id){
        UserDetails userDetails = createUserDetails(id, createMemberDefault());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextImpl securityContext = new SecurityContextImpl(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new CustomUserDetails(
                member.getId(),
                String.valueOf(member.getUsername()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    private UserDetails createUserDetails(Long id, Member notSavedmember) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(notSavedmember.getAuthority().toString());

        return new CustomUserDetails(
                id,
                String.valueOf(notSavedmember.getUsername()),
                notSavedmember.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

}
