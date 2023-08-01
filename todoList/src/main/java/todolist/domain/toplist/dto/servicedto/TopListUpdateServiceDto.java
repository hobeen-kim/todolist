package todolist.domain.toplist.dto.servicedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.toplist.entity.Status;

@Getter
@AllArgsConstructor
@Builder
public class TopListUpdateServiceDto {

    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private Status status;

}
