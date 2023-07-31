package todolist.domain.category.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    @DisplayName("멤버, 이름, 색상으로 카테고리를 생성한다.")
    void createCategory() {
        //given
        Member member = createMember();
        String categoryName = "category name";
        String hexColor = "#FFFFFF";

        //when
        Category category = Category.createCategory(member, categoryName, hexColor);

        //then
        assertThat(category.getMember()).isEqualTo(member);
        assertThat(category.getCategoryName()).isEqualTo(categoryName);
        assertThat(category.getHexColor()).isEqualTo(hexColor);

    }

    @Test
    @DisplayName("카테고리 이름을 변경한다.")
    void changeCategoryName() {
        //given
        Member member = createMember();
        Category category = createCategory(member);

        String newCategoryName = "new category name";

        //when
        category.changeCategoryName(newCategoryName);

        //then
        assertThat(category.getCategoryName()).isEqualTo(newCategoryName);
    }


    @Test
    @DisplayName("카테고리 색상을 변경한다.")
    void changeHexColor() {
        //given
        Member member = createMember();
        Category category = createCategory(member);

        String newHexColor = "#000000";

        //when
        category.changeHexColor(newHexColor);

        //then
        assertThat(category.getHexColor()).isEqualTo(newHexColor);
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

    Category createCategory(Member member){
        return Category.builder()
                .member(member)
                .categoryName("category name")
                .hexColor("#FFFFFF")
                .build();
    }
}