package todolist.domain.todo.repository.searchCond;

import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DateTypeSearchCond {

    LocalDate from;
    LocalDate to;
    SearchType searchType;

}
