package todolist.domain.dayplan.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String content;
    @NotNull(message = "{validation.date}")
    private LocalDate date;
    @NotNull(message = "{validation.time}")
    private LocalTime startTime;
    @NotNull(message = "{validation.time}")
    private LocalTime endTime;
    private Long todoId;

    public static DayPlanCreateServiceDto toServiceDto(DayPlanCreateApiDto dto) {

        return DayPlanCreateServiceDto.builder()
                .content(dto.getContent())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .todoId(dto.getTodoId())
                .build();

    }
}
