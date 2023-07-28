package todolist.domain.member.dto.servicedto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberResponseServiceDto {

    private Long id;
    private String name;
    private String username;
    private String email;
    private Authority authority;
    private LocalDateTime createdDate;

    public static MemberResponseServiceDto of(Member member) {
        return new MemberResponseServiceDto(
                member.getId(),
                member.getName(),
                member.getUsername(),
                member.getEmail(),
                member.getAuthority(),
                member.getCreatedDate()
        );
    }
}
