package todolist.domain.toplist.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.toplist.dto.servicedto.TopListCreateServiceDto;

@Builder
@AllArgsConstructor
@Getter
public class TopListCreateApiDto {

    private String title;
    private String content;
    private Long categoryId;

    public TopListCreateServiceDto toServiceDto() {
        return TopListCreateServiceDto.builder()
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .build();
    }

}
