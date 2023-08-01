package todolist.domain.category.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.category.dto.servicedto.CategoryResponseServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class CategoryResponseApiDto {

    private Long id;
    private String categoryName;
    private String hexColor;
    List<DayPlanResponseServiceDto> dayPlans;
    List<TopListResponseServiceDto> topLists;
    List<TodoResponseServiceDto> todos;

    public static CategoryResponseApiDto of(CategoryResponseServiceDto dto){
        return new CategoryResponseApiDto(
                dto.getId(),
                dto.getCategoryName(),
                dto.getHexColor(),
                dto.getDayPlans(),
                dto.getTopLists(),
                dto.getTodos());
    }
}
