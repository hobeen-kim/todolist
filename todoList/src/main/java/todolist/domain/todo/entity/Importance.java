package todolist.domain.todo.entity;

import lombok.RequiredArgsConstructor;
import todolist.global.entity.BaseEnum;

@RequiredArgsConstructor
public enum Importance implements BaseEnum {

    PURPLE("긴급중요"),
    RED("긴급"),
    BLUE("중요"),
    WHITE("보통");

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
