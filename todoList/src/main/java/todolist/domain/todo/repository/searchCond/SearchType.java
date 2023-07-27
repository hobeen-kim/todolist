package todolist.domain.todo.repository.searchCond;

import lombok.RequiredArgsConstructor;
import todolist.global.entity.BaseEnum;

@RequiredArgsConstructor
public enum SearchType implements BaseEnum {
    START_DATE("시작예정일"),
    DEAD_LINE("마감예정일"),
    DONE_DATE("완료일");

    private final String description;


    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
