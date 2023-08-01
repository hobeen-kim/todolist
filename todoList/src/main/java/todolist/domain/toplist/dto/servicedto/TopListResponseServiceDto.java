package todolist.domain.toplist.dto.servicedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.category.entity.Category;
import todolist.domain.todo.entity.Todo;
import todolist.domain.toplist.entity.Status;
import todolist.domain.toplist.entity.TopList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class TopListResponseServiceDto {

    private Long id;
    private String title;
    private String content;
    private Status status;
    private LocalDate doneDate;
    private boolean isDone;
    private Long CategoryId;
    private List<TodoSummary> todos;

    public static List<TopListResponseServiceDto> of(List<TopList> topList) {

        return topList.stream()
                .map(TopListResponseServiceDto::of)
                .collect(Collectors.toList());
    }

    public static TopListResponseServiceDto of(TopList topList) {
        return new TopListResponseServiceDto(
                topList.getId(),
                topList.getTitle(),
                topList.getContent(),
                topList.getStatus(),
                topList.getDoneDate(),
                topList.isDone(),
                getCategoryId(topList.getCategory()),
                getTodos(topList.getTodos())
        );
    }

    private static Long getCategoryId(Category category) {

        if(category == null){
            return null;
        }
        return category.getId();
    }

    private static List<TodoSummary> getTodos(List<Todo> todos) {

        return todos.stream().map(TodoSummary::of).collect(Collectors.toList());

    }

    @Getter
    @AllArgsConstructor
    public static class TodoSummary {
        private final Long id;
        private final String content;
        private final boolean isDone;

        public static TodoSummary of(Todo todo) {
            return new TodoSummary(todo.getId(), todo.getContent(), todo.isDone());
        }
    }
}
