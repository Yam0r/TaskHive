package user.taskhive.config;

import java.time.LocalDateTime;
import java.util.HashSet;
import my.app.files.dto.task.TaskDto;
import my.app.files.dto.user.UpdateProfileRequestDto;
import my.app.files.dto.user.UpdateUserRoleRequestDto;
import my.app.files.dto.user.UserResponseDto;
import my.app.files.model.Attachment;
import my.app.files.model.Label;
import my.app.files.model.Project;
import my.app.files.model.Role;
import my.app.files.model.Task;
import my.app.files.model.User;

public class TestDataUtil {

    public static Project createTestProject(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    public static Task createTestTask(Project project, String taskName) {
        Task task = new Task();
        task.setName(taskName);
        task.setProject(project);
        return task;
    }

    public static Attachment createTestAttachment(Task task, String filename,
                                                  String dropboxFileId) {
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFilename(filename);
        attachment.setDropboxFileId(dropboxFileId);
        attachment.setUploadDate(LocalDateTime.now());
        return attachment;
    }

    public static Label createTestLabel(String name, String color) {
        Label label = new Label();
        label.setName(name);
        label.setColor(color);
        return label;
    }

    public static User createTestUser(Long id, String email, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(new HashSet<>());
        return user;
    }

    public static Role createTestRole(Long id, Role.RoleName roleName) {
        Role role = new Role();
        role.setId(id);
        role.setRole(roleName);
        return role;
    }

    public static TaskDto createTestTaskDto(Long id, String name) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(id);
        taskDto.setName(name);
        return taskDto;
    }

    public static UserResponseDto createTestUserResponseDto(Long id, String email,
                                                            String firstName, String lastName) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(id);
        userResponseDto.setEmail(email);
        userResponseDto.setFirstName(firstName);
        userResponseDto.setLastName(lastName);
        return userResponseDto;
    }

    public static UpdateUserRoleRequestDto createTestUpdateUserRoleRequestDto(Role.RoleName
                                                                                      roleName) {
        UpdateUserRoleRequestDto requestDto = new UpdateUserRoleRequestDto();
        requestDto.setNewRole(roleName);
        return requestDto;
    }

    public static UpdateProfileRequestDto createTestUpdateProfileRequestDto(Long id,
                                                                            String firstName,
                                                                            String lastName) {
        UpdateProfileRequestDto requestDto = new UpdateProfileRequestDto();
        requestDto.setId(id);
        requestDto.setFirstName(firstName);
        requestDto.setLastName(lastName);
        return requestDto;
    }
}
