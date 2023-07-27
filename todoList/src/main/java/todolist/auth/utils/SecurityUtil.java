package todolist.auth.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import todolist.auth.service.CustomUserDetails;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    public static Long getCurrentId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        validate(authentication);

        return userDetails.getId();
    }

    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validate(authentication);

        return authentication.getName();
    }

    private static void validate(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new MemberNotFoundException();
        }
    }
}
