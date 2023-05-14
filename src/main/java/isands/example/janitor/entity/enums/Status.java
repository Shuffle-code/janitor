package isands.example.janitor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("В работе"), DISABLE("Резерв"), NOT_ACTIVE("В очереди"), DELETED("Черный список");
    private final String title;
}
