package todolist.domain.todo.dto.servicedto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TodoCreateServiceDto {

    private String content;
    private Importance importance;
    private LocalDate startDate;
    private LocalDate deadLine;
}
