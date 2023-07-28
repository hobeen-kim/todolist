package todolist.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import todolist.domain.member.service.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/members")
public class MemberController {

    private final MemberService memberService;

    //todo : 회원가입 기능

    //todo : 회원 정보 조회 기능

    //todo : 비밀번호 변경 기능

    //todo : 회원 탈퇴 기능

    //todo : 권한 변경 기능

}
