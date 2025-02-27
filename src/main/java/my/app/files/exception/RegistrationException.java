package my.app.files.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class RegistrationException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationException.class);

    private final int errorCode;

    public RegistrationException(String message) {
        super(message);
        this.errorCode = 400;
        logException(message);
    }

    public RegistrationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        logException(message);
    }

    private void logException(String message) {
        logger.error("RegistrationException occurred: {}", message);
    }
}
