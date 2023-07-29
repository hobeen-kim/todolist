package todolist.domain.member.dto.apidto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.member.entity.Authority;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberAuthorityApiDto {

    @NotNull(message = "{validation.member.authority}")
    private Authority authority;

}
