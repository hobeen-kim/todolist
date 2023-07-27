package todolist.domain.dayplan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import todolist.domain.dayplan.dto.apidto.request.DayPlanCreateApiDto;
import todolist.domain.dayplan.dto.apidto.request.DayPlanUpdateApiDto;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanUpdateServiceDto;
import todolist.domain.dayplan.service.DayPlanService;
import todolist.global.reponse.ApiResponse;
import todolist.auth.utils.SecurityUtil;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/dayplans")
public class DayPlanController {

    private final DayPlanService dayPlanService;
    private static final String BASE_URL = "v1/api/dayplans";

    /**
     * 특정 기간의 일정 목록을 조회합니다.
     * @param from 조회 시작일
     * @param to 조회 종료일
     * @return 일정 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DayPlanListResponseApiDto>> getDayPlans(LocalDate from, LocalDate to){

        Long memberId = SecurityUtil.getCurrentId();
        List<DayPlanResponseServiceDto> responseServiceDto = dayPlanService.findDayPlanList(memberId, from, to);

        ApiResponse<DayPlanListResponseApiDto> response = buildApiOkResponse(responseServiceDto);

        return ResponseEntity.ok(response);
    }

    /**
     * DayPlan 을 생성합니다.
     * @param dto 생성에 필요한 정보
     * @return 201 응답만 반환합니다.
     */
    @PostMapping
    public ResponseEntity<Void> createDayPlan(@RequestBody @Valid DayPlanCreateApiDto dto){

        Long memberId = SecurityUtil.getCurrentId();

        DayPlanCreateServiceDto serviceDto = DayPlanCreateApiDto.toServiceDto(dto);

        DayPlanResponseServiceDto responseServiceDto = dayPlanService.saveDayPlan(memberId, serviceDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * DayPlan 을 수정합니다.
     * @param dto 수정에 필요한 정보
     * @param dayPlanId 수정할 DayPlan 의 id
     * @return 204 응답만 반환합니다.
     */
    @PatchMapping("/{dayPlanId}")
    public ResponseEntity<Void> updateDayPlan(@RequestBody @Valid DayPlanUpdateApiDto dto, @PathVariable Long dayPlanId){

        Long memberId = SecurityUtil.getCurrentId();

        DayPlanUpdateServiceDto serviceDto = DayPlanUpdateApiDto.toServiceDto(dto);

        dayPlanService.updateDayPlan(memberId, dayPlanId, serviceDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{dayPlanId}")
    public ResponseEntity<Void> deleteDayPlan(@PathVariable Long dayPlanId){

        Long memberId = SecurityUtil.getCurrentId();

        dayPlanService.deleteDayPlan(memberId, dayPlanId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    private ApiResponse<DayPlanListResponseApiDto> buildApiOkResponse(List<DayPlanResponseServiceDto> serviceDto) {
        DayPlanListResponseApiDto responseDto = DayPlanListResponseApiDto.of(serviceDto);
        return ApiResponse.ok(responseDto);
    }

}
