package todolist.domain.dayplan.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.dayplan.repository.searchCond.DateSearchCond;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.global.testHelper.RepositoryTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class DayPlanRepositoryTest extends RepositoryTest {

    @Autowired DayPlanRepository dayPlanRepository;

    @Test
    @DisplayName("dayPlan Id 를 통해 dayPlan 과 member 를 함께 조회한다.")
    public void findByIdWithMember(){
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(dayPlan);
        em.flush();
        em.clear();

        //when
        DayPlan findDayPlan = dayPlanRepository.findByIdWithMember(dayPlan.getId()).orElseThrow();

        //then
        //findDayPlan 의 member 가 프록시 객체가 아닌 것을 확인
        assertThat(Hibernate.isInitialized(findDayPlan.getMember())).isTrue();
    }

    @TestFactory
    @DisplayName("시작날짜, 종료날짜를 통해 DayPlan 을 조회한다.")
    Collection<DynamicTest> findByCond() {

        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        List<DayPlan> dayPlans = createDayPlans(
                category,
                LocalDate.of(2023, 3, 1), 50);
        addDayPlans(member, dayPlans);

        em.persist(category);
        em.persist(member);
        em.flush();
        em.clear();

        return List.of(
                dynamicTest("시작날짜, 종료날짜로 dayPlan 을 조회한다.", () -> {
                    //given
                    DateSearchCond cond = DateSearchCond.builder()
                            .from(LocalDate.of(2023, 3, 1))
                            .to(LocalDate.of(2023, 3, 10))
                            .build();

                    //when
                    List<DayPlan> findDayPlans = dayPlanRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findDayPlans).hasSize(10);
                    assertThat(findDayPlans.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                    assertThat(findDayPlans.get(findDayPlans.size() - 1))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 10));
                }),
                dynamicTest("시작날짜 이후 dayPlan 을 모두 조회한다.", () -> {
                    //given
                    DateSearchCond cond = DateSearchCond.builder()
                            .from(LocalDate.of(2023, 3, 1))
                            .build();

                    //when
                    List<DayPlan> findDayPlans = dayPlanRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findDayPlans).hasSize(50);
                    assertThat(findDayPlans.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                }),
                dynamicTest("마지막 날짜 이전 dayPlan 을 모두 조회한다.", () -> {
                    //given
                    DateSearchCond cond = DateSearchCond.builder()
                            .to(LocalDate.of(2023, 3, 31))
                            .build();

                    //when
                    List<DayPlan> findDayPlans = dayPlanRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findDayPlans).hasSize(31);
                    assertThat(findDayPlans.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                    assertThat(findDayPlans.get(findDayPlans.size() - 1))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 31));
                }),
                dynamicTest("날짜가 모두 null 이면 모든 dayPlan 을 조회한다.", () -> {
                    //given
                    DateSearchCond cond = DateSearchCond.builder()
                            .build();

                    //when
                    List<DayPlan> findDayPlans = dayPlanRepository.findByCond(member.getId(), cond);

                    //then
                    assertThat(findDayPlans).hasSize(50);
                })
        );


    }

    List<DayPlan> createDayPlans(Category category, LocalDate startDate, int count){
        List<DayPlan> dayPlans = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            DayPlan dayPlan = createDayPlan(category, startDate.plusDays(i));
            dayPlans.add(dayPlan);
        }
        return dayPlans;
    }

    DayPlan createDayPlan(Category category, LocalDate date){
        return DayPlan.builder()
                .content("test")
                .category(category)
                .date(date)
                .startTime(LocalTime.of(10, 0, 0))
                .endTime(LocalTime.of(11, 0, 0))
                .build();
    }

    void addDayPlans(Member member, List<DayPlan> dayPlans){
        for (DayPlan dayPlan : dayPlans) {
            member.addDayPlans(dayPlan);
        }
    }
}