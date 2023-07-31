package todolist.domain.category.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;
import todolist.global.entity.BaseEntity;
import todolist.global.entity.PlanEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    private String hexColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<TopList> topLists = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<DayPlan> dayPlans = new ArrayList<>();

    @Builder
    private Category(Member member, String categoryName, String hexColor) {
        this.member = member;
        this.categoryName = categoryName;
        this.hexColor = hexColor;
    }

    //==연관관계 메서드==//
    public void addTopList(TopList topList){
        this.topLists.add(topList);
    }

    public void addTodo(Todo todo){
        this.todos.add(todo);
    }

    public void addDayPlan(DayPlan dayPlan){
        this.dayPlans.add(dayPlan);
    }

    //==생성 메서드==//
    public static Category createCategory(Member member, String categoryName, String hexColor){
        return new Category(member, categoryName, hexColor);
    }

    //==비즈니스 로직==//
    public void changeCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public void changeHexColor(String hexColor){
        this.hexColor = hexColor;
    }
}
