package interaction_api.common;

import lombok.Getter;

@Getter
public enum FallbackUtil {
    FALLBACK_MESSAGE("Сервис временно недоступен");

    private final String message;

    FallbackUtil(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}