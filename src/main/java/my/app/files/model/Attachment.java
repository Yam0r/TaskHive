package my.app.files.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Attachment {
    Long Id;
    String Dropbox_File_id; // Store the reference to Dropbox
    String File_Name;
    LocalDateTime Upload_Time;
}
