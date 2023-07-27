package todolist.global.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockController {

    @GetMapping
    public ResponseEntity<String> mock() {
        return ResponseEntity.ok("mock");
    }

}
