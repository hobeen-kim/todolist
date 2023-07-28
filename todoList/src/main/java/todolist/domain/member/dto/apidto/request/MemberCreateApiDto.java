package todolist.domain.member.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;

@AllArgsConstructor
@Getter
@Builder
public class MemberCreateApiDto {

    private String name;
    private String username;
    private String password;
    private String email;

    public MemberCreateServiceDto toServiceDto() {

        return MemberCreateServiceDto.builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .build();

    }
}
