package sample.chatserver.auth.exception;

public class JwtTokenInvalidException extends RuntimeException {
    public JwtTokenInvalidException(String message) {
        super(message);
    }
}
