package todolist.domain.dayplan.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class DayPlanCreateApiDto {

    @NotBlank(message = "{validation.content}")
    @Size(min = 1, max = 100, message = "{validation.size}")
    private String content;
    @NotNull(message = "{validation.date}")
    private LocalDate date;
    @NotNull(message = "{validation.time}")
    private LocalTime startTime;
    @NotNull(message = "{validation.time}")
    private LocalTime endTime;
    private Long todoId;
    @NotNull(message = "{validation.category}")
    private Long categoryId;

    public DayPlanCreateServiceDto toServiceDto() {

        return DayPlanCreateServiceDto.builder()
                .content(this.content)
                .date(this.date)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .todoId(this.todoId)
                .categoryId(this.categoryId)
                .build();

    }
}
