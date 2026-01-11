package interaction_api.common.exception;


public class ServiceFallBackException extends RuntimeException {

    public ServiceFallBackException(String message) {
        super(message);
    }
}