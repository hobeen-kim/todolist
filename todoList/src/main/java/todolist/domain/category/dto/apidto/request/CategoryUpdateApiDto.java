package todolist.domain.category.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.category.dto.servicedto.request.CategoryUpdateServiceDto;

@AllArgsConstructor
@Getter
@Builder
public class CategoryUpdateApiDto {

    private String categoryName;
    private String hexColor;

    public CategoryUpdateServiceDto toServiceDto(Long categoryId){
        return CategoryUpdateServiceDto.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .hexColor(hexColor)
                .build();
    }
}
