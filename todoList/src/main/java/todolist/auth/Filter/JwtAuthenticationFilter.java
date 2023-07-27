package todolist.auth.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import todolist.auth.dto.LoginDto;
import todolist.auth.utils.AuthConstant;
import todolist.auth.service.TokenProvider;

import java.io.IOException;

import static todolist.auth.utils.AuthConstant.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws ServletException, IOException {
        
        String accessToken = tokenProvider.generateAccessToken(authentication, AuthConstant.ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = tokenProvider.generateRefreshToken(authentication, AuthConstant.REFRESH_TOKEN_EXPIRE_TIME);

        response.setHeader(AUTHORIZATION, BEARER + accessToken);
        response.setHeader(REFRESH, refreshToken);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }
}
