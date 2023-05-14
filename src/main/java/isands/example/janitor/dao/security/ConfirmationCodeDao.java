package isands.example.janitor.dao.security;

import isands.example.janitor.entity.security.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ConfirmationCodeDao extends JpaRepository<ConfirmationCode, Long> {
    ConfirmationCode findConfirmationCodeByAccountUser_Id (Long id);
}
