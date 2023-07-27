package todolist.auth.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import todolist.global.exception.buinessexception.BusinessException;
import todolist.global.reponse.ApiResponse;

import java.io.IOException;

public class AuthUtil {

    public static void sendErrorResponse(HttpServletResponse response, BusinessException exception) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(exception)));
    }
}
