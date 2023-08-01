package todolist.domain.category.service;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.dto.servicedto.CategoryCreateServiceDto;
import todolist.domain.category.dto.servicedto.CategoryUpdateServiceDto;
import todolist.domain.category.entity.Category;
import todolist.domain.category.repository.CategoryRepository;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;
import todolist.global.exception.buinessexception.categoryexception.CategoryAccessDeniedException;
import todolist.global.exception.buinessexception.categoryexception.CategoryNotFoundException;
import todolist.global.testHelper.ServiceTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static todolist.domain.todo.entity.Importance.RED;

class CategoryServiceTest extends ServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired CategoryService categoryService;
    @Autowired CategoryRepository categoryRepository;

    @Test
    @DisplayName("현재 로그인된 사용자의 카테고리를 생성한다.")
    void saveCategory() {
        //given
        CategoryCreateServiceDto dto = CategoryCreateServiceDto.builder()
                .categoryName("categoryName")
                .hexColor("#FFFFFF")
                .build();

        Member member = createMember();
        memberRepository.save(member);

        //when
        Long categoryId = categoryService.saveCategory(member.getId(), dto);

        //then
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        assertThat(category.getCategoryName()).isEqualTo(dto.getCategoryName());
        assertThat(category.getHexColor()).isEqualTo(dto.getHexColor());
    }

    @TestFactory
    @DisplayName("현재 로그인된 사용자의 카테고리를 수정한다.")
    Collection<DynamicTest> updateCategory() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        memberRepository.save(member);
        categoryRepository.save(category);

        return List.of(
                DynamicTest.dynamicTest("카테고리의 이름을 수정한다.", () -> {
                    //given
                    String categoryName = "category update name1";
                    CategoryUpdateServiceDto dto = CategoryUpdateServiceDto.builder()
                            .categoryId(category.getId())
                            .categoryName(categoryName)
                            .build();

                    //when
                    categoryService.updateCategory(member.getId(), dto);

                    //then
                    Category findCategory = categoryRepository.findById(category.getId()).orElseThrow();
                    assertThat(findCategory.getCategoryName()).isEqualTo(categoryName);

                }),
                DynamicTest.dynamicTest("카테고리의 색상을 수정한다.", () -> {
                    //given
                    String hexColor = "#000000";
                    CategoryUpdateServiceDto dto = CategoryUpdateServiceDto.builder()
                            .categoryId(category.getId())
                            .hexColor(hexColor)
                            .build();

                    //when
                    categoryService.updateCategory(member.getId(), dto);

                    //then
                    Category findCategory = categoryRepository.findById(category.getId()).orElseThrow();
                    assertThat(findCategory.getHexColor()).isEqualTo(hexColor);

                }),
                DynamicTest.dynamicTest("이름과 색상을 동시에 수정한다.", () -> {
                    //given
                    String categoryName = "category update name2";
                    String hexColor = "#00FF00";

                    CategoryUpdateServiceDto dto = CategoryUpdateServiceDto.builder()
                            .categoryId(category.getId())
                            .categoryName(categoryName)
                            .hexColor(hexColor)
                            .build();

                    //when
                    categoryService.updateCategory(member.getId(), dto);

                    //then
                    Category findCategory = categoryRepository.findById(category.getId()).orElseThrow();
                    assertThat(findCategory.getCategoryName()).isEqualTo(categoryName);
                    assertThat(findCategory.getHexColor()).isEqualTo(hexColor);
                })
        );
    }

    @Test
    @DisplayName("카테고리 수정 시 현재 로그인된 사용자의 카테고리가 아니면 CategoryAccessDeniedException 이 발생한다.")
    void updateCategoryAccessDeniedException() {
        //given
        Member currentMember = createMember();
        Member otherMember = createMember();
        Category category = createCategory(otherMember); //otherMember 의 카테고리
        memberRepository.save(otherMember);
        categoryRepository.save(category);

        CategoryUpdateServiceDto dto = CategoryUpdateServiceDto.builder()
                .categoryId(category.getId())
                .categoryName("category name")
                .hexColor("#FFFFFF")
                .build();

        //when & then
        assertThatThrownBy(() -> categoryService.updateCategory(currentMember.getId(), dto))
                .isInstanceOf(CategoryAccessDeniedException.class)
                .hasMessage(CategoryAccessDeniedException.MESSAGE);
    }

    @Test
    @DisplayName("카테고리 수정 시 카테고리 id 를 찾을 수 없으면 CategoryNotFoundException 이 발생한다.")
    void updateCategoryNotFoundException() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        memberRepository.save(member);
        categoryRepository.save(category);

        CategoryUpdateServiceDto dto = CategoryUpdateServiceDto.builder()
                .categoryId(category.getId() + 99L) // 존재하지 않는 카테고리 id
                .categoryName("category name")
                .hexColor("#FFFFFF")
                .build();

        //when & then
        assertThatThrownBy(() -> categoryService.updateCategory(member.getId(), dto))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(CategoryNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("현재 로그인된 사용자의 카테고리를 삭제한다. 카테고리와 연관된 todo, TopList, DayPlan 도 삭제된다.")
    void deleteCategory() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        Todo todo = createTodo(member, category);
        TopList topList = createTopList(member, category);
        DayPlan dayPlan = createDayPlan(member, category);

        memberRepository.save(member);
        categoryRepository.save(category);
        em.persist(todo);
        em.persist(topList);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        //when
        categoryService.deleteCategory(member.getId(), category.getId());

        //then
        assertThat(categoryRepository.findById(category.getId())).isEmpty();
        assertThat(em.find(Todo.class, todo.getId())).isNull();
        assertThat(em.find(TopList.class, topList.getId())).isNull();
        assertThat(em.find(DayPlan.class, dayPlan.getId())).isNull();
    }

    @Test
    @DisplayName("카테고리 삭제 시 현재 로그인된 사용자의 카테고리가 아니면 CategoryAccessDeniedException 이 발생한다.")
    void deleteCategoryAccessDeniedException() {
        //given
        Member currentMember = createMember();
        Member otherMember = createMember();
        Category category = createCategory(otherMember); //otherMember 의 카테고리
        memberRepository.save(otherMember);
        categoryRepository.save(category);

        //when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(currentMember.getId(), category.getId()))
                .isInstanceOf(CategoryAccessDeniedException.class)
                .hasMessage(CategoryAccessDeniedException.MESSAGE);
    }

    @Test
    @DisplayName("카테고리 삭제 시 카테고리 id 를 찾을 수 없으면 CategoryNotFoundException 이 발생한다.")
    void deleteCategoryNotFoundException() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        memberRepository.save(member);
        categoryRepository.save(category);

        //when & then
        assertThatThrownBy(() -> categoryService.deleteCategory(member.getId(), category.getId() + 99L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(CategoryNotFoundException.MESSAGE);
    }

    Member createMember() {
        return Member.builder()
                .name("name")
                .username("username")
                .authority(Authority.ROLE_USER)
                .password("1234abcd!")
                .email("email@test.com")
                .build();
    }
}