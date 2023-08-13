package todolist.domain.toplist.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Member;
import todolist.domain.toplist.entity.TopList;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;
import todolist.global.testHelper.RepositoryTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TopListRepositoryTest extends RepositoryTest {

    @Autowired TopListRepository topListRepository;

    @Test
    @DisplayName("topList id 를 통해 member 와 함께 조회한다.")
    void findByIdWithMember() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        TopList topList = createTopList(member, category);

        member.addCategories(category);
        member.addTopLists(topList);

        em.persist(member);

        em.flush();
        em.clear();

        //when
        TopList findTopList = topListRepository.findByIdWithMember(topList.getId()).orElseThrow();

        //then
        //findTopList 의 member 가 프록시 객체가 아닌 것을 확인
        assertThat(Hibernate.isInitialized(findTopList.getMember())).isTrue();
    }

    @TestFactory
    @DisplayName("검색조건(시작날짜, 종료날짜, 완료 일정 포함 여부) 을 통해 TopList 를 조회한다.")
    Collection<DynamicTest> findByCond() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);

        LocalDate doneDate = LocalDate.of(2023, 3, 1);

        List<TopList> topLists = createTopLists(
                member,
                category,
                doneDate,
                10);

        em.persist(category);
        em.persist(member);
        topLists.forEach(em::persist);

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("완료 일정은 포함하지 않는다.", ()->{
                    //given
                    TopListSearchCond cond = new TopListSearchCond(
                            null,
                            null,
                            false,
                            category.getId()
                    );

                    //when
                    List<TopList> findTopLists = topListRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findTopLists).hasSize(5)
                            .extracting("isDone")
                            .containsOnly(false);

                    //초기화 확인
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getMember())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getCategory())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getTodos())).isTrue();


                }),
                dynamicTest("완료 일정을 포함해 검색한다.", ()->{
                    //given
                    TopListSearchCond cond = new TopListSearchCond(
                            null,
                            null,
                            true,
                            category.getId()
                    );

                    //when
                    List<TopList> findTopLists = topListRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findTopLists).hasSize(10);

                    //초기화 확인
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getMember())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getCategory())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getTodos())).isTrue();
                }),
                dynamicTest("완료 일정을 포함해 날짜로 검색한다.", ()->{
                    //given
                    TopListSearchCond cond = new TopListSearchCond( //doneDate 를 기준으로 검색
                            doneDate.plusDays(8),
                            doneDate.plusDays(10),
                            true,
                            category.getId()
                    );

                    //when
                    List<TopList> findTopLists = topListRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findTopLists).hasSize(6); //미완료 5개 + 완료 1개

                    //초기화 확인
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getMember())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getCategory())).isTrue();
                    assertThat(Hibernate.isInitialized(findTopLists.get(0).getTodos())).isTrue();
                })

        );

    }

}