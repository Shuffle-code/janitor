package isands.example.janitor.dto.mapper;

import isands.example.janitor.dto.UserDto;
import isands.example.janitor.entity.security.AccountUser;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    AccountUser toAccountUser(UserDto userDto);
    UserDto toUserDto(AccountUser accountUser);
}
