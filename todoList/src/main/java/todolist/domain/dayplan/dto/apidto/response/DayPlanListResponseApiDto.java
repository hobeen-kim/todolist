package todolist.domain.dayplan.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.global.reponse.PageInfo;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class DayPlanListResponseApiDto {

    private List<DayPlanResponseApiDto> dayPlans;
    private PageInfo pageInfo;

    public static DayPlanListResponseApiDto of(Page<DayPlanResponseServiceDto> page){
        return null;
    }

    public static DayPlanListResponseApiDto of(List<DayPlanResponseServiceDto> dayplans){
        List<DayPlanResponseApiDto> dtos = new ArrayList<>();
        for(DayPlanResponseServiceDto dayplan : dayplans){
            dtos.add(DayPlanResponseApiDto.of(dayplan));
        }

        return new DayPlanListResponseApiDto(dtos, null);
    }

}
