package todolist.domain.toplist.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.domain.toplist.entity.Status;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
public class TopListResponseApiDto {

    private Long id;
    private String title;
    private String content;
    private Status status;
    private LocalDate doneDate;
    private boolean isDone;
    private Long categoryId;
    private List<TopListResponseServiceDto.TodoSummary> todos;

    public static TopListResponseApiDto of(TopListResponseServiceDto dto) {
        return new TopListResponseApiDto(
                dto.getId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getStatus(),
                dto.getDoneDate(),
                dto.isDone(),
                dto.getCategoryId(),
                dto.getTodos()
        );
    }

}
