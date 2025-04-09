package my.app.files.exception;

public class LabelAlreadyExistsException extends RuntimeException {
    public LabelAlreadyExistsException(String message) {
        super(message);
    }
}
