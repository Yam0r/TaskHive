package my.app.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "my.app.files")
public class TaskHiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskHiveApplication.class, args);
    }
}
