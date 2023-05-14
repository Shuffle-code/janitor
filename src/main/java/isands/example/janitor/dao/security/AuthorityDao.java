package isands.example.janitor.dao.security;

import isands.example.janitor.entity.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthorityDao extends JpaRepository<Authority, Long> {
}
