package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class MemberWithdrawalApiDto {

    @NotBlank(message = "{validation.member.password}")
    private String password;

}
