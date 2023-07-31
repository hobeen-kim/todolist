package todolist.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.category.dto.servicedto.CategoryCreateServiceDto;
import todolist.domain.category.dto.servicedto.CategoryUpdateServiceDto;
import todolist.domain.category.entity.Category;
import todolist.domain.category.repository.CategoryRepository;
import todolist.domain.member.entity.Member;
import todolist.global.exception.buinessexception.categoryexception.CategoryAccessDeniedException;
import todolist.global.exception.buinessexception.categoryexception.CategoryNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 카테고리 생성
     * @param memberId 현재 로그인된 사용자의 id
     * @param dto 카테고리 생성에 필요한 정보
     * @return 생성된 카테고리의 id
     */
    public Long saveCategory(Long memberId, CategoryCreateServiceDto dto){

        Member currentMember = customUserDetailsService.loadUserById(memberId);

        Category category = Category.createCategory(currentMember, dto.getCategoryName(), dto.getHexColor());

        categoryRepository.save(category);

        return category.getId();
    }

    /**
     * 카테고리 수정 (return 없음)
     * @param memberId 현재 로그인된 사용자의 id
     * @param dto 카테고리 수정에 필요한 정보
     */
    public void updateCategory(Long memberId, CategoryUpdateServiceDto dto){

        Category category = verifiedCategory(memberId, dto.getCategoryId());

        update(category, dto.getCategoryName(), dto.getHexColor());
    }

    //todo : 카테고리 삭제
    public void deleteCategory(Long memberId, Long categoryId){

        Category category = verifiedCategory(memberId, categoryId);

        categoryRepository.delete(category);
    }

    //todo : 카테고리 전체 내용 조회 (해당날짜부터 4주차까지의 day_plans, 모든 미완료 top_List, DeadLine 이 지나지 않은 미완료 todo)

    private void update(Category category, String categoryName, String hexColor){
        Optional.ofNullable(categoryName)
                .ifPresent(category::changeCategoryName);
        Optional.ofNullable(hexColor)
                .ifPresent(category::changeHexColor);
    }

    private Category verifiedCategory(Long memberId, Long categoryId){
        Category category = categoryRepository.findByIdWithMember(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if(!category.getMember().getId().equals(memberId)){
            throw new CategoryAccessDeniedException();
        }

        return category;
    }
}
