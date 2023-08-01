package todolist.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.category.dto.servicedto.CategoryCreateServiceDto;
import todolist.domain.category.dto.servicedto.CategoryResponseServiceDto;
import todolist.domain.category.dto.servicedto.CategoryUpdateServiceDto;
import todolist.domain.category.entity.Category;
import todolist.domain.category.repository.CategoryRepository;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.dayplan.service.DayPlanService;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.domain.todo.service.TodoService;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;
import todolist.domain.toplist.service.TopListService;
import todolist.global.exception.buinessexception.categoryexception.CategoryAccessDeniedException;
import todolist.global.exception.buinessexception.categoryexception.CategoryNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final TopListService topListService;
    private final DayPlanService dayPlanService;
    private final TodoService todoService;

    /**
     * 카테고리 생성
     * @param memberId 현재 로그인된 사용자의 id
     * @param dto 카테고리 생성에 필요한 정보
     * @return 생성된 카테고리의 id
     */
    @Transactional
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
    @Transactional
    public void updateCategory(Long memberId, CategoryUpdateServiceDto dto){

        Category category = verifiedCategory(memberId, dto.getCategoryId());

        update(category, dto.getCategoryName(), dto.getHexColor());
    }

    @Transactional
    public void deleteCategory(Long memberId, Long categoryId){

        Category category = verifiedCategory(memberId, categoryId);

        categoryRepository.delete(category);
    }

    //todo : 카테고리 전체 내용 조회 (해당날짜의 시작 월요일부터 4주차까지의 day_plans, 모든 미완료 top_List, DeadLine 이 지나지 않은 미완료 todo)
    public CategoryResponseServiceDto getInfoInCategory(Long memberId, Long categoryId, LocalDate today){

        Category category = verifiedCategory(memberId, categoryId);

        //todo : 해당 날짜부터 4주차까지의 day_plans 조회
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusWeeks(4).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<DayPlanResponseServiceDto> dayPlans = dayPlanService.findDayPlanList(memberId, startOfWeek, endOfWeek);

        //todo : 모든 미완료 top_List 조회
        TopListSearchCond topListSearchCond = TopListSearchCond.builder()
                .build();
        List<TopListResponseServiceDto> topLists = topListService.findTopLists(memberId, topListSearchCond);

        //todo : DeadLine 이 지나지 않은 미완료 todo 조회
        List<TodoResponseServiceDto> todos = todoService.findTodoList(memberId, null, null, SearchType.DEAD_LINE);

        return CategoryResponseServiceDto.of(category, dayPlans, topLists, todos);
    }


    private void update(Category category, String categoryName, String hexColor){
        Optional.ofNullable(categoryName)
                .ifPresent(category::changeCategoryName);
        Optional.ofNullable(hexColor)
                .ifPresent(category::changeHexColor);
    }

    public Category verifiedCategory(Long memberId, Long categoryId){
        Category category = categoryRepository.findByIdWithMember(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if(!category.getMember().getId().equals(memberId)){
            throw new CategoryAccessDeniedException();
        }

        return category;
    }
}
