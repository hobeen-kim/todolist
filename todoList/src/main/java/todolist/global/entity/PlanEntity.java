package todolist.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@MappedSuperclass
@Getter
public abstract class PlanEntity extends BaseEntity{

    protected String content;

    protected boolean isDone = false;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    protected Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    protected Category category;

    /**
     * Todo와 Member의 연관관계를 설정한다. member 에서 Todo 를 추가하도록 한다.
     * @param member : 추가할 member
     */
    public void addMember(Member member) {
        this.member = member;
    }

    public abstract void changeCategory(Category category);


}
