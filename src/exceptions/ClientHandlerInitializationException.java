package exceptions;

public class ClientHandlerInitializationException extends RuntimeException {
    public ClientHandlerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
