package isands.example.janitor.service;


import isands.example.janitor.dto.UserDto;
import isands.example.janitor.entity.security.AccountUser;

import java.io.IOException;
import java.util.List;

public interface UserService {

    UserDto register(UserDto userDto) throws IOException;
    UserDto update(UserDto userDto);
    AccountUser findByUsername(String username);
    AccountUser update(AccountUser userDto);
    UserDto findById(Long id);
    List<UserDto> findAll();
    String getConfirmationCode();
    void deleteById(Long id);
    void generateConfirmationCode(UserDto thisUser, String confirmationCode);
}
