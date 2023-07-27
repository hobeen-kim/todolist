package todolist.auth.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import todolist.auth.utils.AuthUtil;
import todolist.auth.service.TokenProvider;
import todolist.global.exception.buinessexception.BusinessException;
import todolist.global.exception.buinessexception.authexception.AuthException;
import todolist.global.exception.buinessexception.authexception.JwtNotFoundAuthException;
import todolist.global.exception.buinessexception.requestexception.RequestNotAllowedException;

import java.io.IOException;

import static todolist.auth.utils.AuthConstant.*;

@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //POST 방식이 아니면 refresh 페이지를 포함해서 다시 예외를 던진다.
        if(!request.getMethod().equals("POST")){
            request.setAttribute(BUSINESS_EXCEPTION, new RequestNotAllowedException());
        }
        else{
            try {
                String refreshToken = getRefreshToken(request);
                if(refreshToken == null){
                    throw new JwtNotFoundAuthException();
                }
                tokenProvider.validateToken(refreshToken);

                String regeneratedAccessToken = tokenProvider.generateAccessTokenFromRefreshToken(refreshToken, ACCESS_TOKEN_EXPIRE_TIME);

                response.setHeader(AUTHORIZATION, BEARER + regeneratedAccessToken);

                return;
            }catch(AuthException exception){
                //refreshToken 도 만료되거나 잘못되어있으면 로그인 페이지를 location 으로 으답한다.
                response.setHeader("Allow", "POST");
                response.setHeader(LOCATION, request.getScheme() + "://" + request.getServerName() +  LOGIN_PATH);
                AuthUtil.sendErrorResponse(response, exception);
                return;
            }catch(BusinessException exception){
                request.setAttribute(BUSINESS_EXCEPTION, exception);
            }catch(Exception exception){
                request.setAttribute(EXCEPTION, exception);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return !request.getRequestURI().equals(REFRESH_URL);
    }

    private String getRefreshToken(HttpServletRequest request) {

        return request.getHeader(REFRESH);
    }
}
