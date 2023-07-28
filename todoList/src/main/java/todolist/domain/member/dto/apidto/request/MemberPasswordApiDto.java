package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberPasswordApiDto {

    @NotBlank(message = "{validation.member.password}")
    private String prevPassword;
    @NotBlank(message = "{validation.member.password}")
    private String newPassword;
}
