package todolist.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.domain.member.dto.MemberCreateServiceDto;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    public Member findMember(Long id) {
        return findMemberById(id);
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

    private void passwordCheck(String prevPassword, Member member) {
        if(!passwordEncoder.matches(prevPassword, member.getPassword()))
            throw new MemberPasswordException();
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

}
