package user.taskhive;

import my.app.files.TaskHiveApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication(scanBasePackages = "my.app.files")
@SpringBootTest(classes = TaskHiveApplication.class)
class TaskHiveApplicationTests {

    @Test
    void contextLoads(){
    }
}
