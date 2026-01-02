package interaction_api.common.exception;

public class LowQuantityException extends RuntimeException {
    public LowQuantityException(String message){
        super(message);
    }
}
