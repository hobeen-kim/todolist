package todolist.domain.toplist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.category.entity.Category;
import todolist.domain.category.service.CategoryService;
import todolist.domain.member.entity.Member;
import todolist.domain.toplist.dto.servicedto.TopListCreateServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListResponseServiceDto;
import todolist.domain.toplist.dto.servicedto.TopListUpdateServiceDto;
import todolist.domain.toplist.entity.TopList;
import todolist.domain.toplist.repository.TopListRepository;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;
import todolist.global.exception.buinessexception.planexception.PlanNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopListService {

    private final TopListRepository topListRepository;
    private final CustomUserDetailsService userDetailsService;
    private final CategoryService categoryService;

    public Long saveTopList(Long memberId, TopListCreateServiceDto dto) {

        Member member = verifiedMember(memberId);
        Category category = categoryService.verifiedCategory(memberId, dto.getCategoryId());

        TopList topList = TopList.createTopList(
                member,
                dto.getTitle(),
                dto.getContent(),
                category
        );

        return topListRepository.save(topList).getId();
    }

    //여기서 TopListSearchCond 를 사용해서 repository - service - controller 가 하나의 dto 를 사용하는 게 맞을까? 이런 고민하는 게 시간 아까운걸까
    public List<TopListResponseServiceDto> findTopLists(Long memberId, TopListSearchCond cond) {

        List<TopList> topLists = topListRepository.findByCond(memberId, cond);

        return TopListResponseServiceDto.of(topLists);
    }

    public void updateTopList(Long memberId, TopListUpdateServiceDto dto) {

        TopList topList = verifiedTopList(memberId, dto.getId());

        update(topList, dto);
    }

    public void deleteTopList(Long memberId, Long topListId) {

        //todo : 조회 쿼리문 없이 바로 삭제해도 될 듯
        verifiedTopList(memberId, topListId);
        topListRepository.deleteById(topListId);
    }

    public TopList verifiedTopList(Long memberId, Long topListId) {

        TopList topList = topListRepository.findByIdWithMember(topListId)
                .orElseThrow(PlanNotFoundException::new);

        if(!topList.getMember().getId().equals(memberId)){
            throw new PlanAccessDeniedException();
        }

        return topList;
    }

    private Member verifiedMember(Long memberId) {
        return userDetailsService.loadUserById(memberId);
    }

    private void update(TopList topList, TopListUpdateServiceDto dto) {

        Optional.ofNullable(dto.getTitle())
                .ifPresent(topList::changeTitle);
        Optional.ofNullable(dto.getContent())
                .ifPresent(topList::changeContent);
        Optional.ofNullable(dto.getStatus())
                .ifPresent(topList::changeStatus);
        Optional.ofNullable(dto.getCategoryId())
                .ifPresent(categoryId -> changeCategory(topList, categoryId));
    }

    private void changeCategory(TopList topList, Long categoryId) {
        Category category = categoryService.verifiedCategory(topList.getMember().getId(), categoryId);
        topList.changeCategory(category);
    }
}
