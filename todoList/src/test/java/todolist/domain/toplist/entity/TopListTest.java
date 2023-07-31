package todolist.domain.toplist.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TopListTest {

    @Test
    @DisplayName("멤버, 제목, 내용, 카테고리로 TopList를 생성한다.")
    void createTopList() {
        //given
        Member member = createMember();
        Category category = createCategory(member);

        String title = "title";
        String content = "content";

        //when
        TopList topList = TopList.createTopList(member, title, content, category);

        //then
        assertThat(topList.getMember()).isEqualTo(member);
        assertThat(topList.getTitle()).isEqualTo(title);
        assertThat(topList.getContent()).isEqualTo(content);
        assertThat(topList.getCategory()).isEqualTo(category);
        assertThat(topList.getStatus()).isEqualTo(Status.NOT_STARTED);
    }

    @TestFactory
    @DisplayName("TopList 의 상태를 변경한다.")
    Collection<DynamicTest> changeStatus() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        //when
        topList.changeStatus(Status.IN_PROGRESS);

        //then
        assertThat(topList.getStatus()).isEqualTo(Status.IN_PROGRESS);

        return List.of(
                DynamicTest.dynamicTest("상태를 IN_PROGRESS 로 변경한다.", () -> {
                    //when
                    topList.changeStatus(Status.IN_PROGRESS);

                    //then
                    assertThat(topList.getStatus()).isEqualTo(Status.IN_PROGRESS);
                }),
                DynamicTest.dynamicTest("상태를 COMPLETED 로 변경하면 doneDate, isDone 이 설정된다.", () -> {
                    //when
                    topList.changeStatus(Status.COMPLETED);

                    //then
                    assertThat(topList.getStatus()).isEqualTo(Status.COMPLETED);
                    assertThat(topList.getDoneDate()).isNotNull();
                    assertThat(topList.isDone()).isTrue();
                }),
                DynamicTest.dynamicTest("상태를 COMPLETED 에서 IN_PROGRESS 로 변경하면 doneDate 가 사라지고 isDone 이 false 가 된다.", () -> {
                    //when
                    topList.changeStatus(Status.IN_PROGRESS);

                    //then
                    assertThat(topList.getStatus()).isEqualTo(Status.IN_PROGRESS);
                    assertThat(topList.getDoneDate()).isNull();
                    assertThat(topList.isDone()).isFalse();
                })
        );
    }

    @Test
    @DisplayName("제목을 변경한다.")
    void changeTitle() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        String newTitle = "new title";

        //when
        topList.changeTitle(newTitle);

        //then
        assertThat(topList.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("내용을 변경한다.")
    void changeContent() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        String newContent = "new content";

        //when
        topList.changeContent(newContent);

        //then
        assertThat(topList.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("카테고리를 변경한다.")
    void changeCategory() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        Category newCategory = createCategory(member);

        //when
        topList.changeCategory(newCategory);

        //then
        assertThat(topList.getCategory()).isEqualTo(newCategory);
        assertThat(newCategory.getTopLists()).contains(topList);
    }

    private Category createCategory(Member member) {
        return Category.createCategory(member, "category name", "#FFFFFF");
    }

    Member createMember(){
        return Member.builder()
                .name("name")
                .username("username")
                .password("1234abcd!")
                .email("email@test.com")
                .authority(Authority.ROLE_USER)
                .build();
    }

    TopList createTopList(Member member, Category category){
        return TopList.builder()
                .member(member)
                .title("title")
                .content("content")
                .category(category)
                .build();
    }


}