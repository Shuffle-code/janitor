package isands.example.janitor.dao;

import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JanitorDao extends JpaRepository<Janitor, Long> {
    List<Janitor> findAllByStatus(Status status);
    List<Janitor> findAllByStatus(Status status, Pageable pageable);
    List<Janitor> findAllByStatus(Status status, Sort sort);
    @Query(value = "SELECT MAX(id) FROM janitor ", nativeQuery = true)
    Long maxId();

//    @Query(value = "SELECT ID_TTWR FROM nsk_tt.player where ID_TTWR != 'null' & ID_TTWR != ''", nativeQuery = true)
//    List<String> getIdTtw();


    Optional<Janitor> findByLastname(String title);
//    @Query(value = "SELECT ID FROM nsk_tt.player where ID_TTWR = :idTtw", nativeQuery = true)
//    Long getJanitorIdByIdTtw(String idTtw);
}
