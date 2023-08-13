package todolist.domain.toplist.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.toplist.dto.servicedto.TopListUpdateServiceDto;
import todolist.domain.toplist.entity.Status;

@AllArgsConstructor
@Getter
public class TopListUpdateApiDto {

    private String title;
    private String content;
    private Long categoryId;
    private Status status;

    public TopListUpdateServiceDto toServiceDto(Long topListId) {
        return TopListUpdateServiceDto.builder()
                .id(topListId)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .status(status)
                .build();
    }

}
