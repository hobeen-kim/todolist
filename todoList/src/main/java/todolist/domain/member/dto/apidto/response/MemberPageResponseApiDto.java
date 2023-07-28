package todolist.domain.member.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.global.reponse.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class MemberPageResponseApiDto {

    private List<MemberResponseApiDto> members;
    private PageInfo pageInfo;

    public static MemberPageResponseApiDto of(Page<MemberResponseServiceDto> page){

        List<MemberResponseApiDto> dtos = page.getContent().stream()
                .map(MemberResponseApiDto::of)
                .toList();


        PageInfo pageInfo = PageInfo.of(page);

        return new MemberPageResponseApiDto(dtos, pageInfo);
    }
}
