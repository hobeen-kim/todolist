package todolist.domain.toplist.repository.searchCond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
public class TopListSearchCond {

    //완료 일정의 경우 from, to 를 사용한다. -> 완료가 안된 건 페이징 안하고 모두 출력할거임
    private LocalDate from;
    private LocalDate to;
    private boolean isDone;
    private Long categoryId;
}
