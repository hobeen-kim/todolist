package todolist.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import todolist.domain.member.dto.MemberCreateServiceDto;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.global.ServiceTest;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest extends ServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("name 과 email, password 을 받아서 Member 를 생성, 저장하고 MemberId 를 반환한다.")
    void saveMember() {
        //given
        MemberCreateServiceDto dto = MemberCreateServiceDto.builder()
                .name("test")
                .username("test")
                .password("1234")
                .build();

        //when
        Long memberId = memberService.saveMember(dto);

        //then
        Member member = em.find(Member.class, memberId);

        assertThat(member.getId()).isEqualTo(memberId);
        assertThat(member.getName()).isEqualTo(dto.getName());
        assertThat(member.getUsername()).isEqualTo(dto.getUsername());
        assertThat(passwordEncoder.matches(dto.getPassword(), member.getPassword())).isTrue();
        assertThat(member.getAuthority()).isEqualTo(Authority.ROLE_USER);
    }

    @Test
    @DisplayName("memberId 를 통해 Member 를 찾는다.")
    void findMember() {

        //given
        Member savedMember = memberRepository.save(createMemberDefault());

        //when
        Member findMember = memberService.findMember(savedMember.getId());

        //then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getName()).isEqualTo(savedMember.getName());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
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

    Member createMemberDefault() {
        return Member.builder()
                .name("test")
                .username("test")
                .password("1234")
                .email("test@test.com")
                .build();
    }

    Member createMember(String password) {
        return Member.builder()
                .name("test")
                .username("test")
                .password(password)
                .email("test@test.com")
                .build();
    }
}