package todolist.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.todo.entity.Todo;
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

    private String name;

    private String username;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private Authority authority = Authority.ROLE_USER;

    // todos 의 생명주기는 member 에 의존한다.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Todo> todos = new ArrayList<>();

    // dayPlans 의 생명주기는 member 에 의존한다.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<DayPlan> dayPlans = new ArrayList<>();

    @Builder
    private Member(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    //==연관관계 메서드==//

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

        return Member.builder()
                .name(name)
                .username(username)
                .password(password)
                .email(email)
                .build();
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
