package todolist.domain.member.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.member.entity.Authority;

@AllArgsConstructor
@Getter
public class MemberResponseApiDto {

    private String name;
    private String username;
    private String password;
    private String email;
    private Authority authority;
    private String createdDate;
}
