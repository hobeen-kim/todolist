package todolist.domain.member.controller;

import com.google.protobuf.Api;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.member.dto.apidto.request.MemberAuthorityApiDto;
import todolist.domain.member.dto.apidto.request.MemberCreateApiDto;
import todolist.domain.member.dto.apidto.request.MemberPasswordApiDto;
import todolist.domain.member.dto.apidto.request.MemberWithdrawalApiDto;
import todolist.domain.member.dto.apidto.response.MemberPageResponseApiDto;
import todolist.domain.member.dto.apidto.response.MemberResponseApiDto;
import todolist.domain.member.dto.servicedto.MemberResponseServiceDto;
import todolist.domain.member.service.MemberService;
import todolist.global.reponse.ApiResponse;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/members")
public class MemberController {

    private final MemberService memberService;
    public static final String MEMBER_URL = "/v1/api/members";

    /**
     * 회원가입 기능입니다.
     * @param dto 회원가입 정보
     * @return 생성된 회원의 id
     */
    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid MemberCreateApiDto dto) {

        memberService.saveMember(dto.toServiceDto());

        URI uri = URI.create(MEMBER_URL + "/my-info");

        return ResponseEntity.created(uri).build();
    }

    /**
     * 현재 로그인된 회원의 정보를 조회합니다.
     * @return 현재 로그인된 회원의 정보
     */
    @GetMapping("/my-info")
    public ResponseEntity<ApiResponse<MemberResponseApiDto>> getMember() {

        Long memberId = SecurityUtil.getCurrentId();

        MemberResponseServiceDto dto = memberService.findMember(memberId);

        ApiResponse<MemberResponseApiDto> response = buildApiOkResponse(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인된 회원의 비밀번호를 변경합니다.
     * @param dto 비밀번호 변경 정보
     * @return no content 204 응답
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid MemberPasswordApiDto dto) {

        Long memberId = SecurityUtil.getCurrentId();

        memberService.changePassword(memberId, dto.getPrevPassword(), dto.getNewPassword());

        return ResponseEntity.noContent().build();
    }

    /**
     * 페이징으로 회원 리스트를 조회합니다. (admin)
     * @param pageable 페이징 정보
     * @return 회원 리스트
     */
    @GetMapping
    public ResponseEntity<ApiResponse<MemberPageResponseApiDto>> getMembers(Pageable pageable) {

        Page<MemberResponseServiceDto> serviceDtoPage = memberService.findMemberList(pageable);

        MemberPageResponseApiDto apiDtoPage = MemberPageResponseApiDto.of(serviceDtoPage);

        ApiResponse<MemberPageResponseApiDto> response = ApiResponse.ok(apiDtoPage);

        return ResponseEntity.ok(response);
    }

    /**
     * 회원의 권한을 변경합니다. (admin)
     * @param dto 권한 변경 정보
     * @return no content 204 응답
     */
    @PatchMapping("/authority/{memberId}")
    public ResponseEntity<Void> changeAuthority(
            @PathVariable("memberId") Long changeId,
            @RequestBody @Valid MemberAuthorityApiDto dto) {

        Long adminId = SecurityUtil.getCurrentId();

        memberService.changeAuthority(adminId, changeId, dto.getAuthority());

        return ResponseEntity.noContent().build();
    }

    /**
     * 회원을 탈퇴시킵니다. admin 은 모든 회원을 탈퇴시킬 수 있습니다.
     * @param dto 탈퇴 정보
     * @return no content 204 응답
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> withdrawal(
            @PathVariable("memberId") Long memberId,
            @RequestBody @Valid MemberWithdrawalApiDto dto) {

        Long currentId = SecurityUtil.getCurrentId();

        memberService.withdrawal(currentId, memberId, dto.getPassword());

        return ResponseEntity.noContent().build();
    }


    private ApiResponse<MemberResponseApiDto> buildApiOkResponse(MemberResponseServiceDto dto) {

        MemberResponseApiDto responseDto = MemberResponseApiDto.of(dto);

        return ApiResponse.ok(responseDto);
    }
}
