package my.app.files.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {
    Long TaskId;
    Long UserId;
    String Text;
    LocalDateTime Timestamp;
}
