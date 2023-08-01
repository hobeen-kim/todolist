package todolist.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.domain.todo.entity.Todo;
import todolist.global.testHelper.ServiceTest;
import todolist.global.exception.buinessexception.memberexception.MemberAccessDeniedException;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static todolist.domain.todo.entity.Importance.RED;

class MemberServiceTest extends ServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("name 과 username, email, password 을 받아서 Member 를 생성, 저장하고 MemberId 를 반환한다.")
    void saveMember() {
        //given
        MemberCreateServiceDto dto = MemberCreateServiceDto.builder()
                .name("test")
                .username("test")
                .email("test@test.com")
                .password("1234")
                .build();

        //when
        Long memberId = memberService.saveMember(dto);

        //then
        Member member = em.find(Member.class, memberId);

        assertThat(member.getId()).isEqualTo(memberId);
        assertThat(member.getName()).isEqualTo(dto.getName());
        assertThat(member.getUsername()).isEqualTo(dto.getUsername());
        assertThat(member.getEmail()).isEqualTo(dto.getEmail());
        assertThat(passwordEncoder.matches(dto.getPassword(), member.getPassword())).isTrue();
        assertThat(member.getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("memberId 를 통해 Member 를 찾는다.")
    void findMember() {

        //given
        Member savedMember = memberRepository.save(createMemberDefault());

        //when
        MemberResponseServiceDto dto = memberService.findMember(savedMember.getId());

        //then
        assertThat(dto.getId()).isEqualTo(savedMember.getId());
        assertThat(dto.getName()).isEqualTo(savedMember.getName());
        assertThat(dto.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(dto.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(dto.getAuthority()).isEqualTo(savedMember.getAuthority());
        assertThat(dto.getCreatedDate()).isEqualTo(savedMember.getCreatedDate());
    }

    @Test
    @DisplayName("memberId 가 DB 에 없으면 MemberNotFoundException 을 던진다.")
    void findMemberException() {
        //given
        Long memberId = 99L;

        //when //then
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> memberService.findMember(memberId));

        assertThat(exception.getMessage()).isEqualTo(MemberNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberNotFoundException.CODE);
    }

    @Test
    @DisplayName("회원 목록을 페이징으로 조회한다.")
    void findMemberList() {
        //given
        List<Member> members = createMembers(100);
        memberRepository.saveAll(members);

        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        Page<MemberResponseServiceDto> memberList = memberService.findMemberList(pageRequest);

        //then
        assertThat(memberList.getTotalElements()).isEqualTo(members.size());
        assertThat(memberList.getTotalPages()).isEqualTo(10);
        assertThat(memberList.getNumber()).isEqualTo(0);
        assertThat(memberList.getSize()).isEqualTo(10);
        assertThat(memberList.getContent()).hasSize(10)
                .extracting("id").contains(members.get(members.size() - 1).getId());
    }

    @Test
    @DisplayName("memberId 를 통해 Member 를 찾아서 비밀번호를 변경한다.")
    void changePassword() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member member = createMember(encodedPassword);
        memberRepository.save(member);

        String newPassword = "12345";

        //when
        memberService.changePassword(member.getId(), password, newPassword);

        //then
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경 시 이전 비밀번호가 다르면 MemberPasswordException 을 발생시킨다.")
    void changePasswordException() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member member = createMember(encodedPassword);
        memberRepository.save(member);

        String newPassword = "12345";

        //when & then
        MemberPasswordException exception = assertThrows(MemberPasswordException.class,
                () -> memberService.changePassword(member.getId(), password + "1", newPassword));//다른 pw

        assertThat(exception.getMessage()).isEqualTo(MemberPasswordException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberPasswordException.CODE);
    }

    @Test
    @DisplayName("회원 탈퇴 시 Member 를 삭제한다. 이때 연관된 todo, dayPlan 도 자동으로 삭제된다.")
    void withdrawal() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member member = createMember(encodedPassword);
        Member member2 = createMemberDefault();

        Category category = createCategory(member);
        Todo todo = createTodo(member, category);
        DayPlan dayPlan = createDayPlan(member, category);

        member.addCategories(category);
        member.addTodos(todo);
        member.addDayPlans(dayPlan);

        memberRepository.save(member);
        memberRepository.save(member2);

        //when
        memberService.withdrawal(member.getId(), member.getId(), password);

        //then
        Member findMember = em.find(Member.class, member.getId());
        assertThat(findMember).isNull();

        Todo findTodo = em.find(Todo.class, todo.getId());
        assertThat(findTodo).isNull();

        DayPlan findDayPlan = em.find(DayPlan.class, dayPlan.getId());
        assertThat(findDayPlan).isNull();
    }

    @Test
    @DisplayName("회원 탈퇴 시 이전 비밀번호가 다르면 MemberPasswordException 을 발생시킨다.")
    void withdrawalException() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member member = createMember(encodedPassword);
        memberRepository.save(member);

        //when & then
        MemberPasswordException exception = assertThrows(MemberPasswordException.class,
                () -> memberService.withdrawal(member.getId(), member.getId(), password + "1"));//다른 pw

        assertThat(exception.getMessage()).isEqualTo(MemberPasswordException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberPasswordException.CODE);
    }

    @Test
    @DisplayName("admin 권한으로 다른 회원을 삭제한다.")
    void withdrawalWithAdmin() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member user = createMember(encodedPassword);

        Member admin = createMember(Authority.ROLE_ADMIN);

        memberRepository.save(user);
        memberRepository.save(admin);

        //when
        memberService.withdrawal(admin.getId(), user.getId(), password);

        //then
        Member findMember = em.find(Member.class, user.getId());
        assertThat(findMember).isNull();
    }

    @Test
    @DisplayName("admin 권한이 아닐 때 다른 권한을 삭제하려고 하면 MemberAccessDeniedException 이 발생한다.")
    void withdrawalWithAdminException() {
        //given
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        Member user = createMember(encodedPassword);

        Member user2 = createMemberDefault();

        memberRepository.save(user);
        memberRepository.save(user2);

        //when & then
        MemberAccessDeniedException exception = assertThrows(MemberAccessDeniedException.class,
                () -> memberService.withdrawal(user2.getId(), user.getId(), password));

        assertThat(exception.getMessage()).isEqualTo(MemberAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberAccessDeniedException.CODE);
    }

    @Test
    @DisplayName("admin 권한이 가진 회원이 다른 회원의 권한을 변경한다.")
    void changeAuthority() {
        //given
        Member admin = createMember(Authority.ROLE_ADMIN);
        Member user = createMember(Authority.ROLE_USER);

        memberRepository.save(admin);
        memberRepository.save(user);

        //when
        memberService.changeAuthority(admin.getId(), user.getId(), Authority.ROLE_ADMIN);

        //then
        assertThat(user.getAuthority()).isEqualTo(Authority.ROLE_ADMIN);
    }

    @Test
    @DisplayName("admin 권한이 아닐 때 다른 회원의 권한을 변경하려면 MemberAccessDeniedException 이 발생한다.")
    void changeAuthorityException() {
        //given
        Member user1 = createMember(Authority.ROLE_USER);
        Member user2 = createMember(Authority.ROLE_USER);

        memberRepository.save(user1);
        memberRepository.save(user2);

        //when & then
        MemberAccessDeniedException exception = assertThrows(MemberAccessDeniedException.class,
                () -> memberService.changeAuthority(user1.getId(), user2.getId(), Authority.ROLE_ADMIN));

        assertThat(exception.getMessage()).isEqualTo(MemberAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(MemberAccessDeniedException.CODE);
    }

    List<Member> createMembers(int count){
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            members.add(createMemberUsername("test " + i));
        }
        return members;
    }

    Member createMember(Authority authority) {
        return Member.builder()
                .name("test")
                .username("test")
                .password("1234")
                .authority(authority)
                .email("test@test.com")
                .build();
    }

    Member createMemberUsername(String username) {
        return Member.builder()
                .name(username)
                .username("test")
                .password("1234")
                .authority(Authority.ROLE_USER)
                .email("test@test.com")
                .build();
    }

    Member createMember(String password) {
        return Member.builder()
                .name("test")
                .username("test")
                .password(password)
                .authority(Authority.ROLE_USER)
                .email("test@test.com")
                .build();
    }

    Todo createTodo(){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .deadLine(LocalDate.of(2023, 7, 21))
                .build();
    }

    DayPlan createDayPlan(){
        return DayPlan.builder()
                .content("test")
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .build();
    }
}