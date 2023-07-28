package todolist.domain.member.dto.apidto.request;

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

    private Authority authority;

}
