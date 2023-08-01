package todolist.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.TopList;
import todolist.global.entity.BaseEntity;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<TopList> topLists = new ArrayList<>();

    // todos 의 생명주기는 member 에 의존한다.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Todo> todos = new ArrayList<>();

    // dayPlans 의 생명주기는 member 에 의존한다.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<DayPlan> dayPlans = new ArrayList<>();

    @Builder
    private Member(String name, String username, String password, String email, Authority authority) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = authority;
    }


    //==연관관계 메서드==//
    public void addCategories(Category category) {
        this.categories.add(category);
        category.addMember(this);
    }

    public void addTopLists(TopList topList) {
        this.topLists.add(topList);
        topList.addMember(this);
    }

    /**
     * Member와 Todo의 연관관계를 설정한다. member 에서 todo를 추가할 수 있도록 한다.
     * @param todo : 추가할 todo
     */
    public void addTodos(Todo todo){
        this.todos.add(todo);
        todo.addMember(this);
    }
    /**
     * Member와 dayPlan 의 연관관계를 설정한다. member 에서 dayPlan 을 추가할 수 있도록 한다.
     * @param dayPlan : 추가할 dayPlan
     */
    public void addDayPlans(DayPlan dayPlan){
        this.dayPlans.add(dayPlan);
        dayPlan.addMember(this);
    }

    //==생성 메서드==//
    public static Member createMember(String name, String username, String password, String email) {

        return new Member(name, username, password, email, Authority.ROLE_USER);
    }

    //==비즈니스 메서드==//

    public void changeAuthority(Authority authority) {
        this.authority = authority;
    }

    /**
     * 비밀번호를 변경한다.
     * @param newPassword : 새로운 비밀번호
     */
    public void changePassword(String newPassword) {

        this.password = newPassword;
    }
}
