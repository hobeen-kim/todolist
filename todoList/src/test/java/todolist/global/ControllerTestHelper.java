package todolist.global;

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public interface ControllerTestHelper {

    default MockHttpServletRequestBuilder getBuilder(String path) {
            return RestDocumentationRequestBuilders
                    .get(getUriWithPath(path))
                .accept(APPLICATION_JSON);
    }

    default MockHttpServletRequestBuilder getBuilder(String path, MultiValueMap<String, String> queryParams) {
        return RestDocumentationRequestBuilders
                .get(getUriWithPath(path))
                .params(queryParams)
                .accept(APPLICATION_JSON);
    }

    default MockHttpServletRequestBuilder getBuilder(String path, Object... uriVariables) {
        return RestDocumentationRequestBuilders
                .get(getUriWithPath(path), uriVariables)
                .accept(APPLICATION_JSON);
    }

    default MockHttpServletRequestBuilder postBuilder(String path, String content) {
        return MockMvcRequestBuilders
                .post(getUriWithPath(path))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(content);
    }

    default MockHttpServletRequestBuilder postBuilder(String path, String content, Object... uriVariables) {
        return RestDocumentationRequestBuilders
                .post(getUriWithPath(path), uriVariables)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(content);
    }

    default MockHttpServletRequestBuilder patchBuilder(String path, String content) {
        return RestDocumentationRequestBuilders
                .patch(getUriWithPath(path))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(content);
    }

    default MockHttpServletRequestBuilder patchBuilder(String path, String content, Object... uriVariables) {
        return RestDocumentationRequestBuilders
                .patch(getUriWithPath(path), uriVariables)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(content);
    }

    default MockHttpServletRequestBuilder deleteBuilder(String path, Object... uriVariables) {
        return RestDocumentationRequestBuilders
                .delete(getUriWithPath(path), uriVariables);
    }

    default MockHttpServletRequestBuilder deleteBuilder(String path, String content, Object... uriVariables) {
        return RestDocumentationRequestBuilders
                .delete(getUriWithPath(path), uriVariables)
                .content(content)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON);
    }

    String getUrl();

    default String getAuthorizationToken(){
        return "Bearer ABC.ABC.ABC";
    }

    default String withDefaultUrl(){
        return "";
    }


    default String getUriWithPath(String path){
        return getUrl() + path;
    }

}
