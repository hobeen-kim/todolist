package todolist.global.testHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static todolist.domain.todo.entity.Importance.RED;

@DataJpaTest
public abstract class RepositoryTest {

    @Autowired protected EntityManagerFactory emf;
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
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .member(member)
                .category(category)
                .build();
    }

    protected Todo createTodoDone(Member member, Category category){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .doneDate(LocalDate.of(2023, 7, 21))
                .member(member)
                .category(category)
                .build();
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
        return DayPlan.builder()
                .content("test")
                .member(member)
                .category(category)
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .build();
    }

    protected DayPlan createDayPlan(Member member, Category category, LocalDate date){
        return DayPlan.builder()
                .content("test")
                .date(date)
                .startTime(LocalTime.of(10, 0, 0))
                .endTime(LocalTime.of(11, 0, 0))
                .member(member)
                .category(category)
                .build();
    }

    protected Category createCategory(Member member) {
        return Category.builder()
                .categoryName("category")
                .hexColor("#FFFFFF")
                .member(member)
                .build();
    }

    protected TopList createTopList(Member member, Category category){
        return TopList.builder()
                .member(member)
                .category(category)
                .title("title")
                .content("content")
                .build();
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
}
