package todolist.domain.toplist.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.category.repository.CategoryRepository;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.dto.servicedto.TopListCreateServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListUpdateServiceDto;
import todolist.domain.toplist.entity.Status;
import todolist.domain.toplist.entity.TopList;
import todolist.domain.toplist.repository.TopListRepository;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;
import todolist.global.exception.buinessexception.categoryexception.CategoryNotFoundException;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;
import todolist.global.testHelper.ServiceTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class TopListServiceTest extends ServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired TopListRepository topListRepository;
    @Autowired TopListService topListService;


    @Test
    @DisplayName("memberId, title, content, categoryId 로 TopList 를 생성한다.")
    void saveTopList() {
        //given
        Member member = memberRepository.save(createMemberDefault());
        Category category = categoryRepository.save(createCategory(member));

        TopListCreateServiceDto dto = TopListCreateServiceDto.builder()
                .title("title")
                .content("content")
                .categoryId(category.getId())
                .build();

        //when
        Long topListId = topListService.saveTopList(member.getId(), dto);

        //then
        TopList findTopList = topListRepository.findById(topListId).orElseThrow();
        assertThat(findTopList.getTitle()).isEqualTo(dto.getTitle());
        assertThat(findTopList.getContent()).isEqualTo(dto.getContent());
        assertThat(findTopList.getCategory().getCategoryName()).isEqualTo(category.getCategoryName());
    }

    @Test
    @DisplayName("검색조건(완료 여부, 완료 일자 기준) 으로 TopList 를 조회한다.")
    void findTopList() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        LocalDate doneDate = LocalDate.of(2023, 3, 1);

        List<TopList> topLists = createTopLists(
                member,
                category,
                doneDate,
                10);

        memberRepository.save(member);

        TopListSearchCond cond = TopListSearchCond.builder() //전체 조회
                .isDone(true)
                .build();

        //when
        List<TopListResponseServiceDto> topList = topListService.findTopLists(member.getId(), cond);

        //then
        assertThat(topList.size()).isEqualTo(10);
    }

    @TestFactory
    @DisplayName("category, title, content, 완료 여부 를 수정할 수 있다.")
    Collection<DynamicTest> updateTopList() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Category otherCategory = createCategory(member);
        TopList topList = createTopList(member, category);

        memberRepository.save(member);

        return List.of(
                dynamicTest("category 만 변경할 수 있다.", () -> {
                    //given
                    TopListUpdateServiceDto dto = TopListUpdateServiceDto.builder()
                            .id(topList.getId())
                            .categoryId(otherCategory.getId())
                            .build();

                    //when
                    topListService.updateTopList(member.getId(), dto);

                    //then
                    assertThat(topList.getCategory().getId()).isEqualTo(otherCategory.getId());


                }),
                dynamicTest("title 만 변경할 수 있다.", () -> {
                    //given
                    TopListUpdateServiceDto dto = TopListUpdateServiceDto.builder()
                            .id(topList.getId())
                            .title("new title")
                            .build();

                    //when
                    topListService.updateTopList(member.getId(), dto);

                    //then
                    assertThat(topList.getTitle()).isEqualTo(dto.getTitle());

                }),
                dynamicTest("content 만 변경할 수 있다.", () -> {
                    //given
                    TopListUpdateServiceDto dto = TopListUpdateServiceDto.builder()
                            .id(topList.getId())
                            .content("new content")
                            .build();

                    //when
                    topListService.updateTopList(member.getId(), dto);

                    //then
                    assertThat(topList.getContent()).isEqualTo(dto.getContent());

                }),
                dynamicTest("만료 여부 상태만 변경할 수 있다. 변경하면 isDone, DoneDate 가 설정된다.", () -> {
                    //given
                    TopListUpdateServiceDto dto = TopListUpdateServiceDto.builder()
                            .id(topList.getId())
                            .status(Status.COMPLETED)
                            .build();

                    //when
                    topListService.updateTopList(member.getId(), dto);

                    //then
                    assertThat(topList.getStatus()).isEqualTo(dto.getStatus());
                    assertThat(topList.getDoneDate()).isNotNull();
                    assertThat(topList.isDone()).isTrue();

                })
        );
    }

    @Test
    @DisplayName("update 시 존재하지 않는 카테고리로 변경하려고 하면 CategoryNotFoundException 예외가 발생한다.")
    void updateTopListException() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        memberRepository.save(member);

        TopListUpdateServiceDto dto = TopListUpdateServiceDto.builder()
                .id(topList.getId())
                .categoryId(category.getId() + 99L) //존재하지 않는 카테고리
                .build();

        //when & then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            topListService.updateTopList(member.getId(), dto);
        });

        assertThat(exception.getMessage()).isEqualTo("존재하지 않거나 삭제된 카테고리입니다.");
        assertThat(exception.getErrorCode()).isEqualTo("CATEGORY-404");
    }

    @Test
    @DisplayName("TopList 를 삭제한다. 삭제하면 연관된 Todo, DayPlan 은 모두 삭제된다.")
    void deleteTopList() {

        Member member = createMemberDefault();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);
        Todo todo = createTodo(member, category);
        DayPlan dayPlan = createDayPlan(member, category);

        todo.addTopList(topList);
        dayPlan.addTodo(todo);

        em.persist(member);

        em.flush();
        em.clear();

        //when
        topListService.deleteTopList(member.getId(), topList.getId());

        //then
        assertThat(em.find(TopList.class, topList.getId())).isEqualTo(null);
        assertThat(em.find(Todo.class, todo.getId())).isEqualTo(null);
        assertThat(em.find(DayPlan.class, dayPlan.getId())).isEqualTo(null);
    }

    @Test
    @DisplayName("TopList 가 해당 로그인된 사용자의 TopList 인지 확인하고 반환한다.")
    void verifiedTopList() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        memberRepository.save(member);

        //when
        TopList verifiedTopList = topListService.verifiedTopList(member.getId(), topList.getId());

        //then
        assertThat(verifiedTopList.getId()).isEqualTo(topList.getId());
    }

    @Test
    @DisplayName("TopList 가 해당 로그인된 사용자의 TopList 가 아니면 PlanAccessDeniedException 예외가 발생한다.")
    void verifiedTopListException() {
        //given
        Member member = createMemberDefault();
        Member otherMember = createMemberDefault();
        Category otherCategory = createCategory(otherMember);
        TopList topList = createTopList(otherMember, otherCategory); //다른 사용자의 TopList

        memberRepository.save(member);
        memberRepository.save(otherMember);

        //when
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class, () -> {
            topListService.verifiedTopList(member.getId(), topList.getId()); //다른 사용자의 TopList 조회
        });

        //then
        assertThat(exception.getMessage()).isEqualTo("접근 권한이 없는 일정입니다.");
        assertThat(exception.getErrorCode()).isEqualTo("PLAN-403");

    }
}