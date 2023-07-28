package todolist.domain.member.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.domain.member.entity.Authority;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MemberResponseApiDto {

    private Long id;
    private String name;
    private String username;
    private String email;
    private Authority authority;
    private LocalDateTime createdDate;

    public static MemberResponseApiDto of(MemberResponseServiceDto dto) {
        return new MemberResponseApiDto(
                dto.getId(),
                dto.getName(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getAuthority(),
                dto.getCreatedDate()
        );
    }
}
