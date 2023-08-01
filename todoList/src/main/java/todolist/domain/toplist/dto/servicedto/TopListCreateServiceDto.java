package todolist.domain.toplist.dto.servicedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TopListCreateServiceDto {

    private String title;
    private String content;
    private Long categoryId;

}
