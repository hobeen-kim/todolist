package todolist.global.testHelper;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static todolist.domain.todo.entity.Importance.RED;

@SpringBootTest
@Transactional
public abstract class ServiceTest {


    @Autowired protected EntityManager em;

    protected Member createMemberDefault() {
        return Member.builder()
                .name("test")
                .username("test")
                .password("1234abcd!")
                .authority(Authority.ROLE_USER)
                .email("email@test.com")
                .build();
    }

    protected Todo createTodo(Member member, Category category){
        Todo todo = Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .member(member)
                .category(category)
               .build();

        member.addTodos(todo);

        return todo;
    }

    protected Todo createTodoDone(Member member, Category category){
        Todo todo = Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .doneDate(LocalDate.of(2023, 7, 21))
                .member(member)
                .category(category)
                .build();

        member.addTodos(todo);

        return todo;
    }

    protected Todo createTodo(Member member, Category category, String content, Importance importance, LocalDate startDate, LocalDate deadLine){

        return Todo.builder()
                .member(member)
                .category(category)
                .content(content)
                .importance(importance)
                .startDate(startDate)
                .deadLine(deadLine)
                .build();
    }

    protected Todo createTodo(Member member, Category category, LocalDate startDate, LocalDate deadLine){

        return Todo.builder()
                .member(member)
                .category(category)
                .content("content")
                .importance(Importance.BLUE)
                .startDate(startDate)
                .deadLine(deadLine)
                .build();
    }

    protected List<Todo> createTodos(Member member, Category category, int count){
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


    protected List<DayPlan> createDayPlans(Member member, Category category, LocalDate startDate, int count){
        List<DayPlan> dayPlans = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            DayPlan dayPlan = createDayPlan(member, category, startDate.plusDays(i));
            dayPlans.add(dayPlan);
        }
        return dayPlans;
    }

    protected DayPlan createDayPlan(Member member, Category category){
        DayPlan dayPlan = DayPlan.builder()
                .content("test")
                .member(member)
                .category(category)
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .build();

        member.addDayPlans(dayPlan);

        return dayPlan;
    }

    protected DayPlan createDayPlan(Member member, Category category, LocalDate date){
        DayPlan dayPlan = DayPlan.builder()
                .content("test")
                .date(date)
                .startTime(LocalTime.of(10, 0, 0))
                .endTime(LocalTime.of(11, 0, 0))
                .member(member)
                .category(category)
                .build();

        member.addDayPlans(dayPlan);

        return dayPlan;
    }

    protected Category createCategory(Member member) {
        Category category = Category.builder()
                .categoryName("category")
                .hexColor("#FFFFFF")
                .member(member)
                .build();

        member.addCategories(category);

        return category;
    }

    protected TopList createTopList(Member member, Category category, String title, String content, LocalDate doneDate){
        return TopList.builder()
                .member(member)
                .category(category)
                .title(title)
                .content(content)
                .doneDate(doneDate)
                .build();
    }

    protected List<TopList> createTopLists(Member member, Category category, LocalDate doneDate, int count){

        List<TopList> topLists = new ArrayList<>();
        LocalDate actualDoneDate = null;

        for (int i = 0; i < count; i++) {

            if(i % 2 == 0) actualDoneDate = null;
            else actualDoneDate = doneDate.plusDays(i);

            TopList topList = createTopList(member, category, "title" + i, "content" + i, actualDoneDate);
            topLists.add(topList);
            member.addTopLists(topList);
        }
        return topLists;
    }

    protected TopList createTopList(Member member, Category category){
        TopList topList = TopList.builder()
                .member(member)
                .title("title")
                .content("content")
                .category(category)
                .build();

        member.addTopLists(topList);

        return topList;
    }
}
