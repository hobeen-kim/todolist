package todolist.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.dto.LoginDto;
import todolist.auth.service.CustomUserDetails;
import todolist.auth.service.TokenProvider;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.global.ControllerTestHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static todolist.auth.utils.AuthConstant.*;

@AutoConfigureRestDocs
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class})
class AuthTest implements ControllerTestHelper {
    public static final String PATH_PREFIX = "http://localhost";

    public static final String MOCK_AUTH_PATH = "/mock";
    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EntityManager em;
    @Autowired private TokenProvider tokenProvider;

    @Override
    public String getUrl() {
        return "/v1/api/auth";
    }

    protected RestDocumentationResultHandler documentHandler;

    @BeforeEach
    void setUp(TestInfo testInfo) {

        String methodName = testInfo.getTestMethod().orElseThrow().getName().toLowerCase();

        documentHandler = document(
                "auth/" + methodName,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );
    }

    @Test
    @DisplayName("로그인에 성공하면 헤더 값으로 accessToken, refreshToken 을 담아서 응답한다.")
    void login() throws Exception {

        //given
        LoginDto loginDto = new LoginDto("test", "1234");
        createMember(loginDto.getUsername(), loginDto.getPassword());

        String content = new ObjectMapper().writeValueAsString(loginDto);

        //when
        ResultActions actions = mockMvc.perform(postBuilder("/login", content));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(header().exists(REFRESH));

        //restDocs
        actions.andDo(
                documentHandler.document(
                requestFields(
                        fieldWithPath("username").description("로그인할 아이디"),
                        fieldWithPath("password").description("로그인할 비밀번호")
                ),
                responseHeaders(
                        headerWithName(AUTHORIZATION).description("accessToken").attributes(getFormat("Bearer ABC.ABC.ABC")),
                        headerWithName(REFRESH).description("refreshToken").attributes(getFormat("ABC.ABC.ABC"))
                )
        ));
    }


    @TestFactory
    @DisplayName("로그인에 실패하면 401에러로 MEMBER-401, \"로그인 정보를 확인해주세요\" 메세지를 담아서 응답한다.")
    Collection<DynamicTest> loginException() {

        //given
        String username = "test";
        String password = "1234";
        createMember(username, password);

        return List.of(
                dynamicTest("아이디가 없거나 맞지 않을 경우", () -> {

                    //given
                    LoginDto loginDto = new LoginDto("test1", password);
                    String content = new ObjectMapper().writeValueAsString(loginDto);
                    //when
                    ResultActions actions = mockMvc.perform(post(LOGIN_PATH)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(jsonPath("$.code").value(401))
                            .andExpect(jsonPath("$.status").value("MEMBER-401"))
                            .andExpect(jsonPath("$.message").value("로그인 정보를 확인해주세요."));
                }),
                dynamicTest("비밀번호가 잘못된 경우", () -> {
                    //given
                    LoginDto loginDto = new LoginDto(username, "12345");
                    String content = new ObjectMapper().writeValueAsString(loginDto);
                    //when
                    ResultActions actions = mockMvc.perform(post(LOGIN_PATH)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(jsonPath("$.code").value(401))
                            .andExpect(jsonPath("$.status").value("MEMBER-401"))
                            .andExpect(jsonPath("$.message").value("로그인 정보를 확인해주세요."));

                })
        );
    }

    @Test
    @DisplayName("accessToken 을 헤더에 담아서 인가가 필요한 요청하면 접근할 수 있다.")
    void authorization() throws Exception {

        //given
        Member member = createMember("test", "1234");
        String accessToken = createAccessToken(member, 5000L);

        //when
        ResultActions actions = mockMvc.perform(get(MOCK_AUTH_PATH)
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(content().string("mock"));
    }

    @Test
    @DisplayName("accessToken 이 만료되면 401 에러와 AUTH-401, \"인증 유효기간이 만료되었습니다.\" 메세지를 담아서 응답한다.")
    void authenticationExpired() throws Exception {
        //given
        Member member = createMember("test", "1234");
        String accessToken = createAccessToken(member, 1L);

        //when
        ResultActions actions = mockMvc.perform(get(MOCK_AUTH_PATH)
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Allow", "POST"))
                .andExpect(header().string(LOCATION, PATH_PREFIX + REFRESH_URL))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.status").value("AUTH-401"))
                .andExpect(jsonPath("$.message").value("인증 유효기간이 만료되었습니다."));
    }

    @Test
    @DisplayName("refreshToken 을 헤더에 담아서 accessToken 을 재발급 받을 수 있다.")
    void refreshToken() throws Exception {
        //given
        Member member = createMember("test", "1234");
        String refreshToken = createRefreshToken(member, 10000L);

        //when
        ResultActions actions = mockMvc.perform(post(REFRESH_URL)
                .header(REFRESH, refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(REFRESH).description("refreshToken").attributes(getFormat("ABC.ABC.ABC"))
                        ),
                        responseHeaders(
                                headerWithName(AUTHORIZATION).description("accessToken").attributes(getFormat("Bearer ABC.ABC.ABC"))
                        )
                )
        );
    }

    @TestFactory
    @DisplayName("accessToken 재발급 시 refreshToken 이 유효하지 않으면 예외를 응답받는다.")
    Collection<DynamicTest> refreshTokenIssueException() {

        //given
        Member member = createMember("test", "1234");


        return List.of(
                dynamicTest("refreshToken 의 만료기간이 지나면 AUTH-401, \"인증 유효기간이 만료되었습니다.\" 메세지를 응답받는다.", () -> {

                    //given
                    String refreshToken = createRefreshToken(member, 1L);

                    //when
                    ResultActions actions = mockMvc.perform(post(REFRESH_URL)
                            .header(REFRESH, refreshToken)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isUnauthorized())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(header().string("Allow", "POST"))
                            .andExpect(header().string(LOCATION, PATH_PREFIX + LOGIN_PATH))
                            .andExpect(jsonPath("$.code").value(401))
                            .andExpect(jsonPath("$.status").value("AUTH-401"))
                            .andExpect(jsonPath("$.message").value("인증 유효기간이 만료되었습니다."));

                }),
                dynamicTest("refreshToken 의 서명이 잘못되면 AUTH-400, \"유효하지 않은 JWT 토큰입니다.\" 메세지를 응답받는다.", () -> {
                    //given
                    String refreshToken = "abc.abc.abc";

                    //when
                    ResultActions actions = mockMvc.perform(post(REFRESH_URL)
                            .header(REFRESH, refreshToken)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(header().string("Allow", "POST"))
                            .andExpect(header().string(LOCATION, PATH_PREFIX + LOGIN_PATH))
                            .andExpect(jsonPath("$.code").value(400))
                            .andExpect(jsonPath("$.status").value("AUTH-400"))
                            .andExpect(jsonPath("$.message").value("유효하지 않은 JWT 토큰입니다."));
                }),
                dynamicTest("Refresh 헤더값이 없다면 AUTH-400, \"토큰 정보가 필요합니다.\" 메세지를 응답받는다.", () -> {

                    //given

                    //when
                    ResultActions actions = mockMvc.perform(post(REFRESH_URL)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(header().string("Allow", "POST"))
                            .andExpect(header().string(LOCATION, PATH_PREFIX + LOGIN_PATH))
                            .andExpect(jsonPath("$.code").value(400))
                            .andExpect(jsonPath("$.status").value("AUTH-400"))
                            .andExpect(jsonPath("$.message").value("토큰 정보가 필요합니다."));

                }),
                dynamicTest("refreshToken 요청 시 POST 방식이 아니면 REQUEST-405, \"Http Method 가 잘못되었습니다.\" 메세지를 응답받는다.", () -> {
                    //given
                    String refreshToken = createRefreshToken(member, 10000L);

                    //when
                    ResultActions actions = mockMvc.perform(get(REFRESH_URL) // get 방식으로 요청
                            .header(REFRESH, refreshToken)
                            .contentType(MediaType.APPLICATION_JSON));

                    //then
                    actions
                            .andExpect(status().isMethodNotAllowed())
                            .andExpect(jsonPath("$.data").doesNotExist())
                            .andExpect(header().string("Allow", "POST"))
                            .andExpect(header().string(LOCATION, PATH_PREFIX + REFRESH_URL))
                            .andExpect(jsonPath("$.code").value(405))
                            .andExpect(jsonPath("$.status").value("REQUEST-405"))
                            .andExpect(jsonPath("$.message").value("Http Method 가 잘못되었습니다."));

                })
        );

    }

    @Test
    @DisplayName("admin 권한이 필요한 리소스는 admin 권한만 접근 가능하다.")
    void adminResource() throws Exception {
        //given
        Member member = createAdmin("admin", "1234");
        String accessToken = createAccessToken(member, 10000L);

        //when
        ResultActions actions = mockMvc.perform(get(MOCK_AUTH_PATH + "/admin")
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(content().string("mockAdmin"));
    }

    @Test
    @DisplayName("admin 권한이 아닐 때 admin 리소스에 접근하면 403 에러를 리턴받는다.")
    void adminResourceException() throws Exception {
        //given
        Member member = createMember("admin", "1234");
        String accessToken = createAccessToken(member, 10000L);

        //when
        ResultActions actions = mockMvc.perform(get(MOCK_AUTH_PATH + "/admin")
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andExpect(status().isForbidden())
        ;
    }


    private String createAccessToken(Member member, long accessTokenExpireTime) {
        UserDetails userDetails = createUserDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return tokenProvider.generateAccessToken(authenticationToken, accessTokenExpireTime);
    }

    private String createRefreshToken(Member member, long refreshTokenExpireTime) {
        UserDetails userDetails = createUserDetails(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return tokenProvider.generateRefreshToken(authenticationToken, refreshTokenExpireTime);
    }

    private Member createMember(String username, String password) {

        Member member = Member.builder()
                .name("test")
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();

        em.persist(member);

        return member;
    }

    private Member createAdmin(String username, String password) {

        Member member = Member.builder()
                .name("test")
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_ADMIN)
                .build();

        em.persist(member);

        return member;
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

    private Attributes.Attribute getFormat(
            final String value){
        return new Attributes.Attribute("format",value);
    }
}