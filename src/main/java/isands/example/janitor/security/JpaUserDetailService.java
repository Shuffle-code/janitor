package isands.example.janitor.security;

import isands.example.janitor.dao.JanitorImageDao;
import isands.example.janitor.dao.security.AccountRoleDao;
import isands.example.janitor.dao.security.AccountUserDao;
import isands.example.janitor.dao.security.ConfirmationCodeDao;
import isands.example.janitor.dto.UserDto;
import isands.example.janitor.dto.mapper.ParticipantMapper;
import isands.example.janitor.dto.mapper.UserMapper;
import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.JanitorImage;
import isands.example.janitor.entity.enums.Status;
import isands.example.janitor.entity.security.AccountRole;
import isands.example.janitor.entity.security.AccountUser;
import isands.example.janitor.entity.security.ConfirmationCode;
import isands.example.janitor.entity.security.enums.AccountStatus;
import isands.example.janitor.exception.UsernameAlreadyExistsException;
import isands.example.janitor.service.JanitorService;
import isands.example.janitor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailService implements UserDetailsService, UserService {

    private final String imageName = "image104-66.jpg";
    private final AccountUserDao accountUserDao;
    private final AccountRoleDao accountRoleDao;
//    private final UserMapper userMapper;
    public final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationCodeDao confirmationCodeDao;
    private final JanitorImageDao janitorImageDao;
    private final JanitorService janitorService;
//    private final ParticipantMapper participantMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(username);
        return accountUserDao.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found")
        );

    }

    @Override
    public String getConfirmationCode() {
        String confirmationCode;
        return confirmationCode = RandomStringUtils.randomNumeric(5);
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (accountUserDao.findByUsername(userDto.getUsername()).isPresent()) {
            throw  new UsernameAlreadyExistsException(String.format(
                    "Пользователь с таким логином %s уже существует", userDto.getUsername()));
        }
        AccountUser accountUser = modelMapper.map(userDto, AccountUser.class);
//        AccountUser accountUser = userMapper.toAccountUser(userDto);
        Janitor janitor = addNewPlayer(accountUser);
//        player.setRating(BigDecimal.valueOf(500));

        janitorService.save(janitor);
        JanitorImage janitorImage = addNewImage(imageName, janitor);
        janitorImageDao.save(janitorImage);
        AccountRole roleUser = accountRoleDao.findByName("ROLE_USER");
        AccountRole roleAdmin = accountRoleDao.findByName("ROLE_ADMIN");
        AccountRole rolePlayer = accountRoleDao.findByName("ROLE_PLAYER");
        long count = accountUserDao.count();
        if(count == 0){
            accountUser.setRoles(Set.of(roleAdmin));
        } else accountUser.setRoles(Set.of(roleUser));
        log.info("Count: " + count);
        log.info(String.valueOf(count == 0));
        accountUser.setStatus(AccountStatus.ACTIVE);
        accountUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        accountUser.setJanitor(janitor);
        AccountUser registeredAccountUser = accountUserDao.save(accountUser);
        log.debug("User with username {} was registered successfully", registeredAccountUser.getUsername());
        return modelMapper.map(registeredAccountUser, UserDto.class);

    }

    public JanitorImage addNewImage(String nameImage, Janitor janitor){
        JanitorImage janitorImage = new JanitorImage();
        if (janitorImageDao.count() != 0){
            janitorImage.setId(janitorImageDao.maxId() + 1);
        }
        janitorImage.setPath(nameImage);
        janitorImage.setJanitor(janitor);
        return janitorImage;
    }

    public Janitor addNewPlayer(AccountUser accountUser){
        Janitor janitor = modelMapper.map(accountUser, Janitor.class);
        if (janitorService.count() != 0){
            janitor.setId(janitorService.maxId() + 1);
        }
        janitor.setStatus(Status.NOT_ACTIVE);
        return janitor;
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) {
        AccountUser user = modelMapper.map(userDto, AccountUser.class);
        if (user.getId() != null) {
            accountUserDao.findById(userDto.getId()).ifPresent(
                    (p) -> {
                        user.setVersion(p.getVersion());
                        user.setStatus(p.getStatus());
                    }
            );
        }
        return modelMapper.map(accountUserDao.save(user), UserDto.class);

    }

    @Override
    public AccountUser findByUsername(String username) {
        return accountUserDao.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found")
        );
    }
    public AccountUser update(AccountUser accountUser) {
        if (accountUser.getId() != null) {
            accountUserDao.findById(accountUser.getId()).ifPresent(
                    (user) -> accountUser.setVersion(user.getVersion())
            );
        }
        return accountUserDao.save(accountUser);
    }

    @Override
    public UserDto findById(Long id) {
        return null;
    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

//    @Override
//    public void generateConfirmationCode(UserDto thisUser, String code) {
//        ConfirmationCode confirmationCode = ConfirmationCode.builder().
//                code(code)
//                .accountUser(userMapper.toAccountUser(thisUser))
//                .build();
//        confirmationCodeDao.save(confirmationCode);
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public UserDto findById(Long id) {
//        return userMapper.toUserDto(accountUserDao.findById(id).orElse(null));
//    }

//    @Override
//    public List<UserDto> findAll() {
//        return accountUserDao.findAll().stream()
//                .map(userMapper::toUserDto)
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        final AccountUser accountUser = accountUserDao.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("User with id %s not found", id)
                )
        );
        disable(accountUser);
        update(accountUser);
    }

    @Override
    public void generateConfirmationCode(UserDto thisUser, String confirmationCode) {

    }

    private void enable(final AccountUser accountUser) {
        accountUser.setStatus(AccountStatus.ACTIVE);
        accountUser.setAccountNonLocked(true);
        accountUser.setAccountNonExpired(true);
        accountUser.setEnabled(true);
        accountUser.setCredentialsNonExpired(true);
    }

    private void disable(final AccountUser accountUser) {
        accountUser.setStatus(AccountStatus.DELETED);
        accountUser.setAccountNonLocked(false);
        accountUser.setAccountNonExpired(false);
        accountUser.setEnabled(false);
        accountUser.setCredentialsNonExpired(false);
    }
}
