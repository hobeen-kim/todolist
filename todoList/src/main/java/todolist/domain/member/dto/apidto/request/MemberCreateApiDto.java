package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;

@AllArgsConstructor
@Getter
@Builder
public class MemberCreateApiDto {

    @NotBlank(message = "{validation.member.name}")
    private String name;
    @NotBlank(message = "{validation.member.username}")
    private String username;
    @NotBlank(message = "{validation.member.password}")
    private String password;
    @NotBlank(message = "{validation.member.email}")
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
