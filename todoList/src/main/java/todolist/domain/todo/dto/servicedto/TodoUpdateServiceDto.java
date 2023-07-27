package todolist.domain.todo.dto.servicedto;

import lombok.*;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TodoUpdateServiceDto {

        private Long id;
        private String content;
        private Importance importance;
        private LocalDate startDate;
        private LocalDate deadLine;
        private LocalDate doneDate;
}
