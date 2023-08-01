package todolist.domain.category.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.global.testHelper.RepositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest extends RepositoryTest {

    @Autowired private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 아이디로 회원과 함께 페치 조회한다.")
    void findCategoryByIdWithMember() {
        //given
        Member member = createMember();
        Category category = createCategory(member);

        em.persist(member);
        em.persist(category);

        em.flush();
        em.clear();

        //when
        Category findCategory = categoryRepository.findByIdWithMember(category.getId()).orElseThrow();

        //then
        //findCategory 의 member 가 프록시 객체가 아닌 것을 확인
        assertThat(Hibernate.isInitialized(findCategory.getMember())).isTrue();
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
}