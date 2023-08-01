package todolist.domain.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.category.dto.apidto.request.CategoryCreateApiDto;
import todolist.domain.category.dto.apidto.request.CategoryUpdateApiDto;
import todolist.domain.category.dto.apidto.response.CategoryResponseApiDto;
import todolist.domain.category.dto.servicedto.CategoryResponseServiceDto;
import todolist.domain.category.service.CategoryService;
import todolist.global.reponse.ApiResponse;

import java.net.URI;
import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private static final String BASE_URL = "/v1/api/categories";

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseApiDto>> getCategory(@PathVariable Long categoryId){

        Long memberId = SecurityUtil.getCurrentId();
        LocalDate today = LocalDate.now();
        CategoryResponseServiceDto serviceResponse = categoryService.getInfoInCategory(memberId, categoryId, today);

        CategoryResponseApiDto apiDto = CategoryResponseApiDto.of(serviceResponse);

        return ResponseEntity.ok(ApiResponse.ok(apiDto));
    }

    @PostMapping
    public ResponseEntity<Void> postCategory(@RequestBody @Valid CategoryCreateApiDto dto){

        Long memberId = SecurityUtil.getCurrentId();
        Long categoryId = categoryService.saveCategory(memberId, dto.toServiceDto());

        URI uri = URI.create(BASE_URL + "/" + categoryId);

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Void> patchCategory(@PathVariable Long categoryId,
                              @RequestBody @Valid CategoryUpdateApiDto dto){

        Long memberId = SecurityUtil.getCurrentId();

        categoryService.updateCategory(memberId, dto.toServiceDto(categoryId));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId){

        Long memberId = SecurityUtil.getCurrentId();

        categoryService.deleteCategory(memberId, categoryId);

        return ResponseEntity.noContent().build();
    }


}
