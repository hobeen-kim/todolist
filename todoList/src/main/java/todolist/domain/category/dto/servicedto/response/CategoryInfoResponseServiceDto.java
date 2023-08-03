package todolist.domain.category.dto.servicedto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryInfoResponseServiceDto {

    private Long id;
    private String categoryName;
    private String hexColor;

    public static List<CategoryInfoResponseServiceDto> of(Member member){

        return member.getCategories().stream().map(CategoryInfoResponseServiceDto::of).toList();

    }

    private static CategoryInfoResponseServiceDto of(Category category){
        return CategoryInfoResponseServiceDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .hexColor(category.getHexColor())
                .build();
    }
}
