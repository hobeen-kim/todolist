package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;

@AllArgsConstructor
@Getter
@Builder
public class MemberCreateApiDto {

    @NotNull(message = "{validation.member.name}")
    @Pattern(regexp = "^[가-힣a-zA-Z]*$", message = "{validation.member.name}")
    @Size(min = 1, max = 15, message = "{validation.size}")
    private String name;

    @NotNull(message = "{validation.member.username}")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "{validation.member.username}")
    @Size(min = 1, max = 15, message = "{validation.size}")
    private String username;

    @NotNull(message = "{validation.member.password}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]*$", message = "{validation.member.password}")
    @Size(min = 9, max = 20, message = "{validation.size}")
    private String password;

    @Email(message = "{validation.member.email}")
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
