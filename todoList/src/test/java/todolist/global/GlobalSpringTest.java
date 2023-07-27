package todolist.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class GlobalSpringTest {

    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected EntityManager em;
}
