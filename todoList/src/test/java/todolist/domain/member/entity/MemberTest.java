package todolist.domain.member.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todolist.domain.todo.entity.Todo;
import todolist.global.exception.buinessexception.memberexception.MemberPasswordException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static todolist.domain.todo.entity.Importance.*;

class MemberTest {

    @Test
    @DisplayName("Member 의 Todo 에 Todo 를 2개 추가한다.")
    void addTodos() {

        //givens
        Member member = createMemberDefault();
        Todo todo1 = createTodoDefault("test1");
        Todo todo2 = createTodoDefault("test2");

        //when
        member.addTodos(todo1);
        member.addTodos(todo2);

        //then
        assertThat(member.getTodos()).hasSize(2)
                .extracting("content")
                .containsExactly("test1", "test2");

    }

    @Test
    @DisplayName("name 과 email 로 Member 를 생성한다.")
    void createMember() {

        //given
        String name = "test";
        String username = "test";
        String password = "1234";
        String email = "test@test.com";

        //when
        Member member = Member.createMember(name, username, password, email);

        //then
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(member.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("권한 정보를 변경한다.")
    void changeAuthority() {
        //given
        Member member = createMemberDefault();

        //when
        member.changeAuthority(Authority.ROLE_ADMIN);

        //then
        assertThat(member.getAuthority()).isEqualTo(Authority.ROLE_ADMIN);

    }

    @Test
    @DisplayName("기존 비밀번호와 새로운 비밀번호를 받아서 비밀번호를 변경한다.")
    void changePassword() {
        //given
        Member member = createMemberDefault();
        String newPassword = "12345";

        //when
        member.changePassword(newPassword);

        //then
        assertThat(member.getPassword()).isEqualTo(newPassword);
    }

    Member createMemberDefault() {
        return Member.builder()
                .name("test")
                .username("test")
                .password("1234")
                .build();
    }

    Todo createTodoDefault(String content) {
        return Todo.builder()
                .content(content)
                .importance(RED)
                .deadLine(LocalDate.of(2023, 7, 21))
                .build();
    }
}