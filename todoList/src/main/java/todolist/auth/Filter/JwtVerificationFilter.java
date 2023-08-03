package todolist.auth.Filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import todolist.auth.service.CustomUserDetails;
import todolist.auth.service.TokenProvider;
import todolist.global.exception.buinessexception.BusinessException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static todolist.auth.utils.AuthConstant.*;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            Claims claims = verifyTokenClaims(request);
            setAuthenticationToContext(claims);

        }catch(BusinessException exception){
            request.setAttribute(BUSINESS_EXCEPTION, exception);
        }catch(Exception exception){
            request.setAttribute(EXCEPTION, exception);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String accessToken = getAccessToken(request);

        return accessToken == null || !accessToken.startsWith(BEARER);
    }

    private Claims verifyTokenClaims(HttpServletRequest request) {

        String accessToken = getAccessToken(request).replace(BEARER, "");

        return tokenProvider.getParseClaims(accessToken);
    }

    private String getAccessToken(HttpServletRequest request) {

        return request.getHeader(AUTHORIZATION);
    }

    private void setAuthenticationToContext(Claims claims) {

        Collection<? extends GrantedAuthority> authorities = getRoles(claims);

        CustomUserDetails principal = new CustomUserDetails(claims.get(ID_KEY, Long.class), claims.getSubject(), "", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<SimpleGrantedAuthority> getRoles(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
