package todolist.domain.toplist.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.global.entity.PlanEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class TopList extends PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Status status = Status.NOT_STARTED;

    private LocalDate doneDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "topList", cascade = CascadeType.ALL)
    private List<Todo> todos = new ArrayList<>();

    @Builder
    private TopList(Member member, String title, String content, Category category) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    //==연관 관계 메서드==//
    public void changeCategory(Category category){
        this.category = category;
        category.addTopList(this);
    }

    public void addTodo(Todo todo){
        this.todos.add(todo);
    }

    //==생성 메서드==//
    public static TopList createTopList(Member member, String title, String content, Category category){
        return new TopList(member, title, content, category);
    }

    //==비즈니스 로직==//
    public void changeStatus(Status status){
        this.status = status;
        //status 가 완료면 doneDate 설정, isDone 설정
        if(status == Status.COMPLETED){
            this.doneDate = LocalDate.now();
            this.isDone = true;
        }else{
            this.doneDate = null;
            this.isDone = false;
        }

    }

    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }
}
