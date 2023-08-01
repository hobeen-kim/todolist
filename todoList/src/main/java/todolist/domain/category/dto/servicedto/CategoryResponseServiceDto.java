package todolist.domain.category.dto.servicedto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryResponseServiceDto {

    private Long id;
    private String categoryName;
    private String hexColor;
    List<DayPlanResponseServiceDto> dayPlans;
    List<TopListResponseServiceDto> topLists;
    List<TodoResponseServiceDto> todos;

    public static CategoryResponseServiceDto of(Category category,
                                                List<DayPlanResponseServiceDto> dayPlans,
                                                List<TopListResponseServiceDto> topLists,
                                                List<TodoResponseServiceDto> todos){
        return CategoryResponseServiceDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .hexColor(category.getHexColor())
                .dayPlans(dayPlans)
                .topLists(topLists)
                .todos(todos)
                .build();
    }



}
