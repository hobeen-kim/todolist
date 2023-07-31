package todolist.domain.toplist.entity;

import lombok.AllArgsConstructor;
import todolist.global.entity.BaseEnum;

@AllArgsConstructor
public enum Status implements BaseEnum {
    //미시작, 진행중, 완료
    NOT_STARTED("미시작"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료");

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
