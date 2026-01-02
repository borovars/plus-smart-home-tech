package interaction_api.feign.warehouse.model.exception;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException (String message){
        super(message);
    }
}
