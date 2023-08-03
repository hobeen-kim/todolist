package todolist.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConstant {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String REFRESH = "Refresh";
    public static final String Auth_PATH = "/v1/api/auth";
    public static final String LOGIN_PATH = "/v1/api/auth/login";
    public static final String REFRESH_URL = "/v1/api/auth/refresh";
    public static final String LOCATION = "Location";
    public static final String AUTHORITIES_KEY = "auth";
    public static final String ID_KEY = "id";
    public static final String BUSINESS_EXCEPTION = "businessException";
    public static final String EXCEPTION = "exception";
    public static long ACCESS_TOKEN_EXPIRE_TIME;
    public static long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${jwt.access-token-expire-time}")
    public void setAccessTokenExpireTime(long value) {
        ACCESS_TOKEN_EXPIRE_TIME = value;
    }

    @Value("${jwt.refresh-token-expire-time}")
    public void setRefreshTokenExpireTime(long value) {
        REFRESH_TOKEN_EXPIRE_TIME = value;
    }
}
