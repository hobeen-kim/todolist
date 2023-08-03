package todolist.domain.category.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.category.dto.servicedto.response.CategoryInfoResponseServiceDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class CategoryInfoResponseApiDto {

    private Long id;
    private String categoryName;
    private String hexColor;

    public static List<CategoryInfoResponseApiDto> of(List<CategoryInfoResponseServiceDto> dto){

        return dto.stream().map(CategoryInfoResponseApiDto::of).toList();
    }

    private static CategoryInfoResponseApiDto of(CategoryInfoResponseServiceDto dto){
        return new CategoryInfoResponseApiDto(
                dto.getId(),
                dto.getCategoryName(),
                dto.getHexColor());
    }
}
