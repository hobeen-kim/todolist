package todolist.global.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import todolist.TodoListApplication;
import todolist.domain.member.entity.Authority;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.global.entity.BaseEnum;
import todolist.global.exception.buinessexception.memberexception.MemberNotFoundException;
import todolist.global.reponse.ApiResponse;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping
    public ResponseEntity<String> mock() {
        return ResponseEntity.ok("mock");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> mockAdmin() {
        return ResponseEntity.ok("mockAdmin");
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> mockResponseSuccess() {

        ApiResponse<String> response = ApiResponse.ok("success data");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/errors")
    public ResponseEntity<Void> mockResponseError() {

        throw new MemberNotFoundException();
    }

    @PostMapping("/errors/validation")
    public ResponseEntity<Void> mockResponseErrorValidation(@RequestBody @Valid SampleRequest dto) {

        return ResponseEntity.ok().build();
    }

    @PostMapping("/enums")
    public ResponseEntity<Map<String,Map<String, String>>> findEnums(@RequestBody List<String> paths) throws Exception {

        //ocp 를 위한 온몸 비틀기
        Map<String,Map<String, String>> enums = new HashMap<>();

        for(String path : paths) {
            Class clazz = Class.forName(path);
            enums.put(clazz.getSimpleName(), getDocs((BaseEnum[]) clazz.getMethod("values").invoke(null)));
        }

        return ResponseEntity.ok(enums);
    }

    private Map<String, String> getDocs(BaseEnum[] baseEnums) {
        return Arrays.stream(baseEnums)
                .collect(Collectors.toMap(BaseEnum::getName, BaseEnum::getDescription));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SampleRequest {

        @NotBlank(message="{validation.member.name}")
        private String name;

        @Email(message="{validation.member.email}")
        private String email;
    }

}
