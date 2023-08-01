package todolist.domain.toplist.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.toplist.dto.apidto.request.TopListCreateApiDto;
import todolist.domain.toplist.dto.apidto.request.TopListUpdateApiDto;
import todolist.domain.toplist.dto.apidto.response.TopListListResponseApiDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;
import todolist.domain.toplist.service.TopListService;
import todolist.global.reponse.ApiResponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/api/toplist")
@RequiredArgsConstructor
public class TopListController {

    private final TopListService topListService;
    private static final String BASE_URL = "/v1/api/toplist";

    @GetMapping
    public ResponseEntity<ApiResponse<TopListListResponseApiDto>> getTopList(LocalDate from, LocalDate to, Boolean isDone) {

        Long memberId = SecurityUtil.getCurrentId();
        TopListSearchCond searchCond = TopListSearchCond.builder()
                .from(from)
                .to(to)
                .isDone(isDone)
                .build();

        List<TopListResponseServiceDto> serviceResponse = topListService.findTopLists(memberId, searchCond);

        TopListListResponseApiDto apiResponse = TopListListResponseApiDto.of(serviceResponse);

        return ResponseEntity.ok(ApiResponse.ok(apiResponse));
    }

    @PostMapping
    public ResponseEntity<Void> postTopList(@RequestBody @Valid TopListCreateApiDto dto) {

        Long memberId = SecurityUtil.getCurrentId();
        Long topList = topListService.saveTopList(memberId, dto.toServiceDto());

        //todo : 필요없는 uri, 이걸 사용할지 고민해보자 (일단 사용 x)
        URI uri = URI.create(BASE_URL + "/" + topList);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{topListId}")
    public ResponseEntity<Void> patchTopList(@PathVariable Long topListId, @Valid TopListUpdateApiDto dto) {

        Long memberId = SecurityUtil.getCurrentId();

        topListService.updateTopList(memberId, dto.toServiceDto(topListId));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{topListId}")
    public ResponseEntity<Void> deleteTopList(@PathVariable Long topListId) {

        Long memberId = SecurityUtil.getCurrentId();
        topListService.deleteTopList(memberId, topListId);

        return ResponseEntity.noContent().build();
    }
}
