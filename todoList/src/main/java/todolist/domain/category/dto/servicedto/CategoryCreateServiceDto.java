package todolist.domain.category.dto.servicedto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryCreateServiceDto {

    private String categoryName;
    private String hexColor;
}
