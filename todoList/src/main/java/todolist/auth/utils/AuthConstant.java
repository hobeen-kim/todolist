package todolist.auth.utils;

import org.springframework.beans.factory.annotation.Value;

public class AuthConstant {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String REFRESH = "Refresh";
    public static final String LOGIN_PATH = "/v1/auth/login";
    public static final String REFRESH_URL = "/v1/auth/refresh";
    public static final String LOCATION = "Location";
    public static final String AUTHORITIES_KEY = "auth";
    public static final String ID_KEY = "id";
    public static final String BUSINESS_EXCEPTION = "businessException";
    public static final String EXCEPTION = "exception";
    @Value("${jwt.access-token-expire-time}")
    public static long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${jwt.refresh-token-expire-time}")
    public static long REFRESH_TOKEN_EXPIRE_TIME;
}
