package todolist.domain.category.dto.servicedto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CategoryUpdateServiceDto {

    private Long categoryId;
    private String categoryName;
    private String hexColor;
}
