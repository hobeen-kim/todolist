package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class MemberWithdrawalApiDto {

    @NotNull(message = "{validation.member.password}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]*$", message = "{validation.member.password}")
    @Size(min = 9, max = 20, message = "{validation.size}")
    private String password;

}
