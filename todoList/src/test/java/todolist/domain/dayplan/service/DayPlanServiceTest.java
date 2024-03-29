package todolist.domain.dayplan.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanUpdateServiceDto;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.dayplan.repository.DayPlanRepository;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.TodoRepository;
import todolist.global.testHelper.ServiceTest;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;
import todolist.global.exception.buinessexception.planexception.PlanNotFoundException;
import todolist.global.exception.buinessexception.planexception.PlanTimeValidException;
import todolist.global.exception.buinessexception.planexception.dayplanexception.DayPlanTodoDoneException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DayPlanServiceTest extends ServiceTest {

    @Autowired DayPlanService dayPlanService;
    @Autowired MemberRepository memberRepository;
    @Autowired TodoRepository todoRepository;
    @Autowired DayPlanRepository dayPlanRepository;

    @Test
    @DisplayName("content, date, 시작, 종료시간을 받아서 DayPlan 을 생성한다.")
    void saveDayPlan() {

        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        em.persist(member);
        em.persist(category);

        em.flush();
        em.clear();

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .categoryId(category.getId())
                .build();

        //when
        DayPlanResponseServiceDto response = dayPlanService.saveDayPlan(member.getId(), dto);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getContent()).isEqualTo(dto.getContent());
        assertThat(response.isDone()).isFalse();
        assertThat(response.getDate()).isEqualTo(dto.getDate());
        assertThat(response.getStartTime()).isEqualTo(dto.getStartTime());
        assertThat(response.getEndTime()).isEqualTo(dto.getEndTime());
        assertThat(response.getTodoId()).isNull();
        assertThat(response.getCategoryId()).isNotNull();
    }

    @Test
    @DisplayName("DayPlan 생성 시 Todo 를 함께 받아서 생성해서 생성과 즉시 Todo 와 연관관계를 맺는다.")
    void saveDayPlanWithTodo() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Todo todo = createTodo(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(todo);

        em.flush();
        em.clear();

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .todoId(todo.getId())
                .categoryId(category.getId())
                .build();

        //when
        DayPlanResponseServiceDto response = dayPlanService.saveDayPlan(member.getId(), dto);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getContent()).isEqualTo(dto.getContent());
        assertThat(response.isDone()).isFalse();
        assertThat(response.getDate()).isEqualTo(dto.getDate());
        assertThat(response.getStartTime()).isEqualTo(dto.getStartTime());
        assertThat(response.getEndTime()).isEqualTo(dto.getEndTime());
        assertThat(response.getTodoId()).isEqualTo(todo.getId());
        assertThat(response.getCategoryId()).isNotNull();
    }

    @Test
    @DisplayName("DayPlan 생성 시 받는 Todo 가 완료된 상태면 DayPlanTodoDoneException 예외를 발생시킨다.")
    void saveDayPlanTodoDoneException() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Todo todo = createTodoDone(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(todo);

        em.flush();
        em.clear();

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .todoId(todo.getId())
                .categoryId(category.getId())
                .build();

        //when //then
        DayPlanTodoDoneException exception = assertThrows(DayPlanTodoDoneException.class,
                () -> dayPlanService.saveDayPlan(member.getId(), dto));

        assertThat(exception.getMessage()).isEqualTo(DayPlanTodoDoneException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(DayPlanTodoDoneException.CODE);
    }

    @Test
    @DisplayName("DayPlan 생성 시 없는 member id 로 생성하면 MemberNotFoundException 예외를 발생시킨다.")
    void saveDayPlanMemberException() {
        //given
        Member member = createMemberDefault();
        memberRepository.save(member);

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        //when //then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> dayPlanService.saveDayPlan(member.getId() + 99, dto)); // 없는 member id

        assertThat(exception.getMessage()).isEqualTo(MemberNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberNotFoundException.CODE);

    }

    @Test
    @DisplayName("DayPlan 생성 시 받는 Todo id 가 DB 에 없으면 PlanNotFoundException 예외를 발생시킨다.")
    void saveDayPlanTodoNotFoundException() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Todo todo = createTodo(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(todo);

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .todoId(todo.getId() + 99) // 없는 todo id
                .categoryId(category.getId())
                .build();

        //when //then
        PlanNotFoundException exception = assertThrows(PlanNotFoundException.class,
                () -> dayPlanService.saveDayPlan(member.getId(), dto)); // 없는 todo id

        assertThat(exception.getMessage()).isEqualTo(PlanNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanNotFoundException.CODE);

    }

    @Test
    @DisplayName("DayPlan 생성 시 받는 Todo 가 다른 멤버의 Todo 면 PlanAccessDeniedException 예외를 발생시킨다.")
    void saveDayPlanTodoPlanAccessDeniedExceptionException() {
        //given
        Member member1 = createMemberDefault();
        Member member2 = createMemberDefault();
        Category category = createCategory(member1);
        Todo todo = createTodo(member2, category);

        em.persist(member1);
        em.persist(member2);
        em.persist(category);
        em.persist(todo);

        memberRepository.save(member1);
        memberRepository.save(member2);

        DayPlanCreateServiceDto dto = DayPlanCreateServiceDto.builder()
                .content("content")
                .date(LocalDate.of(2023, 7, 21))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .todoId(todo.getId()) // member2 의 todo 를 조회
                .categoryId(category.getId())
                .build();

        //when //then
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class,
                () -> dayPlanService.saveDayPlan(member1.getId(), dto));

        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }

    @TestFactory
    @DisplayName("시작, 종료 날짜를 입력받아 해당 날짜 사이에 있는 dayPlan 리스트를 찾는다.")
    Collection<DynamicTest> findAll() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        List<DayPlan> dayPlans = createDayPlans(member, category,
                LocalDate.of(2023, 3, 1), 50);

        em.persist(member);
        em.persist(category);
        dayPlans.forEach(em::persist);

        return List.of(
                dynamicTest("startDate, endDate 으로 검색한다.",()->{
                    //given
                    LocalDate startDate = LocalDate.of(2023, 3, 1);
                    LocalDate endDate = LocalDate.of(2023, 3, 31);

                    //when
                    List<DayPlanResponseServiceDto> dayPlanList = dayPlanService.findDayPlanList(member.getId(), startDate, endDate);

                    //then
                    assertThat(dayPlanList).hasSize(31);
                    assertThat(dayPlanList.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                    assertThat(dayPlanList.get(dayPlanList.size() - 1))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 31));
                }),
                dynamicTest("startDate 만으로 검색하면 해당 날짜 이후 dayPlan 을 모두 찾는다.",()->{
                    //given
                    LocalDate startDate = LocalDate.of(2023, 4, 1);

                    //when
                    List<DayPlanResponseServiceDto> dayPlanList = dayPlanService.findDayPlanList(member.getId(), startDate, null);

                    //then
                    assertThat(dayPlanList).hasSize(19);
                    assertThat(dayPlanList.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 4, 1));
                }),
                dynamicTest("endDate 만으로 검색하면 해당 날짜 이전 dayPlan 을 모두 찾는다.",()->{
                    //given
                    LocalDate endDate = LocalDate.of(2023, 3, 31);

                    //when
                    List<DayPlanResponseServiceDto> dayPlanList = dayPlanService.findDayPlanList(member.getId(), null, endDate);

                    //then
                    assertThat(dayPlanList).hasSize(31);
                    assertThat(dayPlanList.get(0))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                    assertThat(dayPlanList.get(dayPlanList.size() - 1))
                            .extracting("date")
                            .isEqualTo(LocalDate.of(2023, 3, 31));
                }),
                dynamicTest("검색 조건에 날짜가 둘 다 null 이면 전부 검색한다.",()->{
                    //given

                    //when
                    List<DayPlanResponseServiceDto> dayPlanList = dayPlanService.findDayPlanList(member.getId(), null, null);

                    //then
                    assertThat(dayPlanList).hasSize(dayPlans.size());
                })
        );
    }

    @TestFactory
    @DisplayName("DayPlan 업데이트 값을 받아 DayPlan 을 수정한다.")
    Collection<DynamicTest> updateDayPlan() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        Todo todo = createTodo(member, category);
        Todo todo2 = createTodo(member, category);
        DayPlan dayPlan = createDayPlan(member, category);
        dayPlan.addTodo(todo);
        dayPlan.addTodo(todo2);

        em.persist(member);
        em.persist(category);
        em.persist(todo);
        em.persist(todo2);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        return List.of(
            dynamicTest("dayPlan 의 content 를 업데이트 한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .content("update content")
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getContent()).isEqualTo(dto.getContent());
            }),
            dynamicTest("dayPlan 의 date 를 업데이트 한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .date(LocalDate.of(2023, 7, 21))
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getDate()).isEqualTo(dto.getDate());
            }),
            dynamicTest("dayPlan 의 startTime 을 업데이트 한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .startTime(LocalTime.of(2, 0))
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getStartTime()).isEqualTo(dto.getStartTime());
            }),
            dynamicTest("dayPlan 의 endTime 을 업데이트 한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .endTime(LocalTime.of(22, 0))
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getEndTime()).isEqualTo(dto.getEndTime());
            }),
            dynamicTest("dayPlan 의 isDone 을 업데이트 한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .isDone(true)
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.isDone()).isEqualTo(dto.getIsDone());
            }),
            dynamicTest("dayPlan 의 isDone 을 값을 주지 않으면 그대로 위에서 업데이트한대로 True 를 유지한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.isDone()).isTrue();
            }),
            dynamicTest("dayPlan 에서 todo 와 연관관계를 끊는다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .deleteTodo(true)
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getTodo()).isNull();
            }),
            dynamicTest("dayPlan 에서 새로운 todo2 를 추가한다.", ()-> {
                //given
                DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                        .todoId(todo2.getId())
                        .build();

                //when
                dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);

                //then
                DayPlan findDayPlan = dayPlanRepository.findById(dayPlan.getId()).orElseThrow();
                assertThat(findDayPlan.getTodo().getId()).isEqualTo(dto.getTodoId());
            })
        );
    }

    @TestFactory
    @DisplayName("업데이트 시 시작시간보다 종료시간이 더 빠르면 PlanTimeValidException 가 발생한다.")
    Collection<DynamicTest> updateDayPlanTimeValidException() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("시작시간과 종료시간 둘 다 입력할 경우 예외", () -> {
                    //given
                    LocalTime startTime = dayPlan.getEndTime().plusHours(1);
                    LocalTime endTime = dayPlan.getStartTime().minusHours(1);
                    DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                            .startTime(startTime)
                            .endTime(endTime)
                            .build();

                    //when & then
                    PlanTimeValidException exception = assertThrows(PlanTimeValidException.class, () -> {
                        dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);
                    });

                    assertThat(exception.getMessage()).isEqualTo(PlanTimeValidException.MESSAGE);
                    assertThat(exception.getErrorCode()).isEqualTo(PlanTimeValidException.CODE);
                }),
                dynamicTest("시작시간만 입력할 경우 예외", () -> {
                    //given
                    LocalTime startTime = dayPlan.getEndTime().plusHours(1);
                    DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                            .startTime(startTime)
                            .build();

                    //when & then
                    PlanTimeValidException exception = assertThrows(PlanTimeValidException.class, () -> {
                        dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);
                    });

                    assertThat(exception.getMessage()).isEqualTo(PlanTimeValidException.MESSAGE);
                    assertThat(exception.getErrorCode()).isEqualTo(PlanTimeValidException.CODE);

                }),
                dynamicTest("종료시간만 입력할 경우 예외", () -> {
                    //given
                    LocalTime endTime = dayPlan.getStartTime().minusHours(1);
                    DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                            .endTime(endTime)
                            .build();

                    //when & then
                    PlanTimeValidException exception = assertThrows(PlanTimeValidException.class, () -> {
                        dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto);
                    });

                    assertThat(exception.getMessage()).isEqualTo(PlanTimeValidException.MESSAGE);
                    assertThat(exception.getErrorCode()).isEqualTo(PlanTimeValidException.CODE);
                })
        );

    }

    @Test
    @DisplayName("DayPlan 업데이트 시 존재하지 않는 dayPlanId 를 입력하면 PlanNotFoundException 예외가 발생한다.")
    void updateDayPlanPlanNotFoundException() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                .content("수정된 내용")
                .build();

        //when & then
        PlanNotFoundException exception = assertThrows(PlanNotFoundException.class, () -> {
            dayPlanService.updateDayPlan(member.getId(), dayPlan.getId() + 99L, dto); //존재하지 않는 dayPlanId
        });

        assertThat(exception.getMessage()).isEqualTo(PlanNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanNotFoundException.CODE);
    }

    @Test
    @DisplayName("DayPlan 업데이트 시 다른 회원의 dayPlanId 를 입력하면 PlanAccessDeniedException 이 발생한다.")
    void updateDayPlanPlanAccessDeniedException() {
        //given
        Member member = createMemberDefault();
        Member member2 = createMemberDefault();
        Category category = createCategory(member2);
        DayPlan dayPlan = createDayPlan(member2, category);

        em.persist(member);
        em.persist(member2);
        em.persist(category);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                .content("수정된 내용")
                .build();

        //when & then
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class, () -> {
            dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto); //로그인 member = 1 // dayPlanId 소유자 = 2
        });

        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);

    }

    @Test
    @DisplayName("DayPlan 업데이트 시 존재하지 않는 todoId 를 입력하면 PlanNotFoundException 예외가 발생한다.")
    void updateDayPlanPlanNotFoundException2() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                .todoId(1L) // 존재하지 않는 todoId
                .build();

        //when & then
        PlanNotFoundException exception = assertThrows(PlanNotFoundException.class, () -> {
            dayPlanService.updateDayPlan(member.getId(), dayPlan.getId(), dto); //존재하지 않는 todoId 적용
        });

        assertThat(exception.getMessage()).isEqualTo(PlanNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanNotFoundException.CODE);
    }

    @Test
    @DisplayName("DayPlan 업데이트 시 다른 회원의 todoId 를 입력하면 PlanAccessDeniedException 이 발생한다.")
    void updateDayPlanPlanAccessDeniedException2() {
        //given
        Member member1 = createMemberDefault(); //현재 로그인된 회원
        Category category1 = createCategory(member1);
        Member member2 = createMemberDefault(); //다른 회원
        Category category2 = createCategory(member2);

        DayPlan dayPlan = createDayPlan(member1, category1);
        Todo todo = createTodo(member2, category2);

        em.persist(member1);
        em.persist(member2);
        em.persist(category1);
        em.persist(category2);
        em.persist(dayPlan);
        em.persist(todo);

        em.flush();
        em.clear();

        DayPlanUpdateServiceDto dto = DayPlanUpdateServiceDto.builder()
                .todoId(todo.getId()) // 다른 회원(2) 이 소유한 todoId
                .build();

        //when & then
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class, () -> {
            dayPlanService.updateDayPlan(member1.getId(), dayPlan.getId(), dto); //로그인 회원(1) 이 다른 회원(2) 의 todoId 적용
        });

        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }

    @Test
    @DisplayName("dayPlanId 를 통해 dayPlan 을 삭제한다. 연관된 todo 는 삭제되지 않는다.")
    void deleteDayPlan() {
        //given
        Member member = createMemberDefault();
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member, category);
        Todo todo = createTodo(member, category);

        em.persist(member);
        em.persist(category);
        em.persist(dayPlan);
        em.persist(todo);

        em.flush();
        em.clear();

        //when
        dayPlanService.deleteDayPlan(member.getId(), dayPlan.getId());

        //then
        DayPlan findDayPlan = em.find(DayPlan.class, dayPlan.getId());
        Todo findTodo = em.find(Todo.class, todo.getId());

        assertThat(findDayPlan).isNull();
        assertThat(findTodo).isNotNull();
    }

    @Test
    @DisplayName("dayPlan 을 삭제할 때 다른 member 의 dayPlanId 를 입력하면 PlanAccessDeniedException 이 발생한다.")
    void deleteDayPlanPlanAccessDeniedException() {
        //given
        Member member = createMemberDefault(); // 현재 로그인된 회원 (1)
        Member member2 = createMemberDefault(); // 다른 회원 (2)
        Category category = createCategory(member);
        DayPlan dayPlan = createDayPlan(member2, category); // 다른 회원 (2) 이 소유한 dayPlan

        em.persist(member);
        em.persist(member2);
        em.persist(category);
        em.persist(dayPlan);

        em.flush();
        em.clear();

        //when & then
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class, () -> {
            dayPlanService.deleteDayPlan(member.getId(), dayPlan.getId()); // 다른 회원 (2) 이 소유한 dayPlanId
        });

        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }

}