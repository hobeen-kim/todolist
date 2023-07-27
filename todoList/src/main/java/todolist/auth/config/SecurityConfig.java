package todolist.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import todolist.auth.Filter.JwtAuthenticationFilter;
import todolist.auth.Filter.JwtRefreshFilter;
import todolist.auth.Filter.JwtVerificationFilter;
import todolist.auth.handler.MemberAccessDeniedHandler;
import todolist.auth.handler.MemberAuthenticationEntryPoint;
import todolist.auth.handler.MemberAuthenticationFailureHandler;
import todolist.auth.handler.MemberAuthenticationSuccessHandler;
import todolist.auth.service.TokenProvider;

import static todolist.auth.utils.AuthConstant.LOGIN_PATH;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .cors(getCorsConfigurerCustomizer())
                .exceptionHandling(
                        exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                                .accessDeniedHandler(new MemberAccessDeniedHandler())
                )
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/v1/auth/login").permitAll()
                                .anyRequest().authenticated())
                .apply(new CustomFilterConfigurer())
                ;

        return http.build();
    }

    private Customizer<CorsConfigurer<HttpSecurity>> getCorsConfigurerCustomizer() {

        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return corsConfigurer -> corsConfigurer.configurationSource(source);
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, tokenProvider);
            jwtAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(tokenProvider);
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(tokenProvider);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtRefreshFilter, JwtAuthenticationFilter.class)
                    .addFilterAfter(jwtVerificationFilter, JwtRefreshFilter.class);
        }
    }
}
