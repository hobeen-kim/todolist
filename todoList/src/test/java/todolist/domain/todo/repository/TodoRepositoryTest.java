package todolist.domain.todo.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.searchCond.DateTypeSearchCond;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.global.testHelper.RepositoryTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static todolist.domain.todo.entity.Importance.RED;

class TodoRepositoryTest extends RepositoryTest {


    @Autowired TodoRepository todoRepository;

    @Test
    @DisplayName("Todo Id 를 통해 Todo 와 member 를 함께 조회한다.")
    public void findByIdWithMember(){

        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Todo todo = createTodo(member, category);

        member.addCategories(category);
        member.addTodos(todo);

        em.persist(member);

        em.flush();
        em.clear();

        //when
        Todo findTodo = todoRepository.findByIdWithMember(todo.getId()).orElseThrow();

        //then
        //findTodo 의 member 가 프록시 객체가 아닌 것을 확인
        assertThat(Hibernate.isInitialized(findTodo.getMember())).isTrue();
    }


    @TestFactory
    @DisplayName("검색 시작 날짜와 끝 날짜를 통해 Todo 를 조회한다.")
    Collection<DynamicTest> findByCond() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        List<Todo> todos = createTodos(member, category, 50);

        em.persist(member);
        em.persist(category);
        todos.forEach(em::persist);

        em.flush();
        em.clear();

        return List.of(
            dynamicTest("검색 조건을 start date 로 한다.", () -> {
                //given
                DateTypeSearchCond cond = DateTypeSearchCond.builder()
                        .searchType(SearchType.START_DATE)
                        .from(LocalDate.of(2023, 3, 1))
                        .to(LocalDate.of(2023, 3, 31))
                        .build();

                //when
                List<Todo> findTodos = todoRepository.findByCond(member.getId(), cond);

                //then
                assertThat(findTodos).hasSize(31);
                assertThat(findTodos.get(0))
                        .extracting("startDate")
                        .isEqualTo(LocalDate.of(2023, 3, 1));
                assertThat(findTodos.get(findTodos.size() - 1))
                        .extracting("startDate")
                        .isEqualTo(LocalDate.of(2023, 3, 31));

            }),
            dynamicTest("검색 조건을 deadLine 으로 한다.", () -> {
                //given
                DateTypeSearchCond cond = DateTypeSearchCond.builder()
                        .searchType(SearchType.DEAD_LINE)
                        .from(LocalDate.of(2023, 4, 1))
                        .to(LocalDate.of(2023, 4, 30))
                        .build();

                //when
                List<Todo> findTodos = todoRepository.findByCond(member.getId(), cond);

                //then
                assertThat(findTodos).hasSize(30);
                assertThat(findTodos.get(0))
                        .extracting("deadLine")
                        .isEqualTo(LocalDate.of(2023, 4, 1));
                assertThat(findTodos.get(findTodos.size() - 1))
                        .extracting("deadLine")
                        .isEqualTo(LocalDate.of(2023, 4, 30));

            }),
            dynamicTest("검색 조건을 doneDate 로 한다.", () -> {
                //given
                DateTypeSearchCond cond = DateTypeSearchCond.builder()
                        .searchType(SearchType.DONE_DATE)
                        .from(LocalDate.of(2023, 3, 15))
                        .to(LocalDate.of(2023, 3, 31))
                        .build();

                //when
                List<Todo> findTodos = todoRepository.findByCond(member.getId(), cond);

                //then
                assertThat(findTodos).hasSize(17);
                assertThat(findTodos.get(0))
                        .extracting("doneDate")
                        .isEqualTo(LocalDate.of(2023, 3, 15));
                assertThat(findTodos.get(findTodos.size() - 1))
                        .extracting("doneDate")
                        .isEqualTo(LocalDate.of(2023, 3, 31));
            }),
            dynamicTest("검색 조건에 날짜가 없으면 완료되지 않은 todo 를 모두 검색한다.", () -> {
                //given
                DateTypeSearchCond cond = DateTypeSearchCond.builder()
                        .searchType(SearchType.DONE_DATE)
                        .from(null)
                        .to(null)
                        .build();

                //when
                List<Todo> findTodos = todoRepository.findByCond(member.getId(), cond);

                //then
                assertThat(findTodos).hasSize(0);
            })
        );

    }

    Todo createTodo(Member member, Category category, String content, Importance importance, LocalDate startDate, LocalDate deadLine){

        return Todo.builder()
                .member(member)
                .category(category)
                .content(content)
                .importance(importance)
                .startDate(startDate)
                .deadLine(deadLine)
                .build();
    }

    List<Todo> createTodos(Member member, Category category, int count){
        List<Todo> todos = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate deadline = LocalDate.of(2023, 4, 1);
        LocalDate doneDate = LocalDate.of(2023, 3, 15);
        for (int i = 0; i < count; i++) {
            Importance importance = Importance.values()[i % 3];
            Todo todo = createTodo(
                    member, category,
                    "content " + (i + 1), importance, startDate.plusDays(i), deadline.plusDays(i));
            todo.isDone(doneDate.plusDays(i));
            todos.add(todo);
        }
        return todos;
    }

    void addTodos(Member member, List<Todo> todos){
        for (Todo todo : todos) {
            member.addTodos(todo);
        }
    }
}