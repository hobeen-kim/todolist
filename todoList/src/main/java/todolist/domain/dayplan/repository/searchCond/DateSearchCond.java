package todolist.domain.dayplan.repository.searchCond;

import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class DateSearchCond {

    LocalDate from;
    LocalDate to;
}
