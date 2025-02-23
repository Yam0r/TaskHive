package model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Attachment {
    Long TaskId;
    String Dropbox_File_id; // Store the reference to Dropbox
    String File_Name;
    LocalDateTime Upload_Time;
}
