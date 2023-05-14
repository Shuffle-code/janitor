package isands.example.janitor.dto.mapper;

import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.security.AccountUser;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    AccountUser toAccountUser(Janitor janitor);
    Janitor toJanitor(AccountUser accountUser);
}
