package todolist.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.member.dto.servicedto.MemberCreateServiceDto;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.global.exception.buinessexception.memberexception.MemberAccessDeniedException;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 기능입니다.
     * @param dto 회원가입 정보
     * @return 생성된 회원의 id
     */
    @Transactional
    public Long saveMember(MemberCreateServiceDto dto) {

        Member member = Member.createMember(
                dto.getName(),
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getEmail())
        ;

        return memberRepository.save(member).getId();
    }

    /**
     * 회원 정보를 조회합니다.
     * @param id 조회할 회원의 id
     * @return 조회된 회원
     */
    public MemberResponseServiceDto findMember(Long id) {

        Member member = findMemberById(id);

        return MemberResponseServiceDto.of(member);
    }

    /**
     * 비밀번호를 변경합니다.
     * @param id 변경할 회원의 id
     * @param prevPassword 이전 비밀번호 (plain)
     * @param newPassword 새로운 비밀번호 (plain)
     */
    @Transactional
    public void changePassword(Long id, String prevPassword, String newPassword){

        Member member = findMemberById(id);

        passwordCheck(prevPassword, member);

        member.changePassword(passwordEncoder.encode(newPassword));
    }

    /**
     * 회원 탈퇴 기능입니다.
     * @param id 탈퇴할 회원의 id
     * @param password 회원의 비밀번호 (plain)
     */
    @Transactional
    public void withdrawal(Long id, String password) {

        Member member = findMemberById(id);

        passwordCheck(password, member);

        memberRepository.delete(member);
    }

    /**
     * 권한을 변경합니다. Role_ADMIN 만 접근 가능한 기능입니다.
     * @param id admin 의 ID
     * @param changeId 권한을 변경할 회원의 ID
     * @param authority 변경할 권한
     */
    @Transactional
    public void changeAuthority(Long id, Long changeId, Authority authority) {

        Member member = findMemberById(id);

        verifyAdmin(member);

        Member changeMember = findMemberById(changeId);

        changeMember.changeAuthority(authority);
    }

    private void verifyAdmin(Member member) {
        if(!member.getAuthority().equals(Authority.ROLE_ADMIN))
            throw new MemberAccessDeniedException();
    }

    private void passwordCheck(String prevPassword, Member member) {
        if(!passwordEncoder.matches(prevPassword, member.getPassword()))
            throw new MemberPasswordException();
    }

    private Member findMemberById(Long id) {
        return userDetailsService.loadUserById(id);
    }

}
