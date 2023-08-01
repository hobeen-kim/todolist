package todolist.domain.category.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.category.dto.servicedto.CategoryCreateServiceDto;

@AllArgsConstructor
@Getter
@Builder
public class CategoryCreateApiDto {

    private String categoryName;
    private String hexColor;

    public CategoryCreateServiceDto toServiceDto(){
        return CategoryCreateServiceDto.builder()
                .categoryName(categoryName)
                .hexColor(hexColor)
                .build();
    }
}
