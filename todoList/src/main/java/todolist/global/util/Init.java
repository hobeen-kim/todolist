package todolist.global.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;

import java.time.LocalDate;
import java.time.LocalTime;

//@Component
@RequiredArgsConstructor
public class Init {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.init();
    }

//    @Component
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void init(){
            Member member = Member.createMember(
                    "test",
                    "test",
                    passwordEncoder.encode("1q2w3e4r!"),
                    "test@test.com");

            Category category = Category.createCategory(member, "category1", "#ffb457");
            Category category2 = Category.createCategory(member, "category2", "#ff96bd");
            Category category3 = Category.createCategory(member, "category3", "#9999fb");

            member.addCategories(category);
            member.addCategories(category2);
            member.addCategories(category3);

            TopList topList = TopList.createTopList(member, "topList title1-1", "topList content1-1", category);
            TopList topList2 = TopList.createTopList(member, "topList title1-2", "topList content1-2", category);

            TopList topList3 = TopList.createTopList(member, "topList title2-1", "topList content2-1", category2);
            TopList topList4 = TopList.createTopList(member, "topList title2-2", "topList content2-2", category2);

            TopList topList5 = TopList.createTopList(member, "topList title3-1", "topList content3-1", category3);
            TopList topList6 = TopList.createTopList(member, "topList title3-2", "topList content3-2", category3);

            member.addTopLists(topList);
            member.addTopLists(topList2);
            member.addTopLists(topList3);
            member.addTopLists(topList4);
            member.addTopLists(topList5);
            member.addTopLists(topList6);

            Todo todo = Todo.createTodo(member, category, "todo1", Importance.RED, LocalDate.now().minusDays(4), LocalDate.now().plusDays(1));
            Todo todo2 = Todo.createTodo(member, category, "todo2", Importance.BLUE, LocalDate.now().minusDays(3), LocalDate.now().plusDays(2));
            Todo todo3 = Todo.createTodo(member, category, "todo3", Importance.PURPLE, LocalDate.now().minusDays(2), LocalDate.now().plusDays(3));

            todo.addTopList(topList);
            todo2.addTopList(topList2);

            member.addTodos(todo);
            member.addTodos(todo2);
            member.addTodos(todo3);

            DayPlan dayPlan = DayPlan.createDayPlan(member, category, "dayPlan1", LocalDate.now(), LocalTime.of(12, 0), LocalTime.of(13, 0));
            DayPlan dayPlan2 = DayPlan.createDayPlan(member, category, "dayPlan2", LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0));
            DayPlan dayPlan3 = DayPlan.createDayPlan(member, category, "dayPlan2", LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0));

            dayPlan.addTodo(todo);
            dayPlan3.addTodo(todo3);

            member.addDayPlans(dayPlan);
            member.addDayPlans(dayPlan2);

            em.persist(member);
        }

    }
}
