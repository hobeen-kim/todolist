package todolist.domain.toplist.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.global.reponse.PageInfo;

import java.util.List;

@AllArgsConstructor
@Getter
public class TopListListResponseApiDto {

    private List<TopListResponseApiDto> topLists;
    private PageInfo pageInfo;

    public static TopListListResponseApiDto of(List<TopListResponseServiceDto> topLists) {

        List<TopListResponseApiDto> dtos = topLists.stream()
                .map(TopListResponseApiDto::of)
                .toList();

        return new TopListListResponseApiDto(dtos, null);
    }

}
