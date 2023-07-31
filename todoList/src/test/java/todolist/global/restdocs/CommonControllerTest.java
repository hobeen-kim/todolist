package todolist.global.restdocs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.ResultActions;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.domain.toplist.entity.Status;
import todolist.global.entity.BaseEnum;
import todolist.global.restdocs.util.CustomResponseFieldsSnippet;
import todolist.global.testHelper.ControllerTest;
import todolist.global.common.CommonController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todolist.global.restdocs.util.CustomResponseFieldsSnippet.*;

public class CommonControllerTest extends ControllerTest {

    @Override
    public String getUrl() {
        return "/common";
    }

    @Test
    @DisplayName("기본 api")
    void basicAPI() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(getBuilder("/success"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("data").type(VARIES).description("응답 데이터 (본문)"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("예외 api (validation)")
    void exceptionValidAPI() throws Exception {
        //given
        CommonController.SampleRequest request = new CommonController.SampleRequest("", "test.test");

        String content = objectMapper.writeValueAsString(request);

        //when
        ResultActions actions = mockMvc.perform(postBuilder("/errors/validation", content));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메세지"),
                                fieldWithPath("data").type(ARRAY).description("예외 리스트"),
                                fieldWithPath("data[].field").type(STRING).description("예외 발생 필드"),
                                fieldWithPath("data[].value").type(STRING).description("예외 발생 값"),
                                fieldWithPath("data[].reason").type(STRING).description("예외 발생 이유")
                        )
                ));
    }

    @Test
    @DisplayName("예외 api (business)")
    void exceptionAPI() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(getBuilder("/errors"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("status").type(STRING).description("예외 상태"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("data").type(NULL).description("null 값")
                        )
                ));

    }

    @Test
    @DisplayName("restdocs 용 enum 조회 api")
    void enums() throws Exception {
        //given
        List<String> enumValues = creatEnumRequest(
                Authority.class, Importance.class, SearchType.class, Status.class);

        String content = objectMapper.writeValueAsString(enumValues);

        //when
        ResultActions actions = mockMvc.perform(postBuilder("/enums", content));

        //then
        Map<String, Map<String, String>> enums = objectMapper.readValue(actions.andReturn().getResponse().getContentAsString(), HashMap.class);

        // enum 문서화를 위한 Snippets 생성
        Snippet[] snippets = new Snippet[enums.size()];
        for(int i = 0; i < enums.size(); i++) {

            String enumName = enums.keySet().toArray()[i].toString();

            snippets[i] = customResponseFields("custom-response",
                    beneathPath(enumName).withSubsectionId(enumName),
                    attributes(getTitle(enumName)),
                    enumConvertFieldDescriptor(enums.get(enumName))
            );
        }

        actions
                .andExpect(status().isOk())
                .andDo(
                        documentHandler.document(
                                snippets
                        )
                );

    }

    // Map으로 넘어온 enumValue 를 fieldWithPath 로 변경하여 리턴
    private FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    @SafeVarargs
    private List<String> creatEnumRequest(Class<? extends BaseEnum>... enums) {

        List<String> enumValues = new ArrayList<>();
            for (Class<?> e : enums) {

                enumValues.add(e.getName());
        }
        return enumValues;
    }
}
