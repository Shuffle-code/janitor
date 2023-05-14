package isands.example.janitor.dao;

import isands.example.janitor.entity.JanitorImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JanitorImageDao extends JpaRepository<JanitorImage, Long> {

    JanitorImage findFirstByJanitorId(Long id);

    @Query(value = "SELECT janitor_image.path FROM janitor_image WHERE janitor_image.janitor_id = :id LIMIT 1", nativeQuery = true)
    String findImageNameByJanitorId(@Param("id") Long id);

    @Query(value = "SELECT janitor_image.path FROM janitor_image WHERE janitor_image.id = :id LIMIT 1", nativeQuery = true)
    String findImageNameByImageId(@Param("id") Long id);

    @Query(value = "SELECT janitor_image.id FROM janitor_image WHERE janitor_image.path = :path LIMIT 1", nativeQuery = true)
    Long findImageIdByPath(String path);

    @Query(value = "SELECT janitor_image.id from janitor_image WHERE janitor_image.janitor_id = :id", nativeQuery = true)
    List<Long> findAllIdImagesByJanitorId(@Param("id") Long id);

    @Query(value = "SELECT MAX(id) FROM janitor_image ", nativeQuery = true)
    Long maxId();

    @Query(value = "SELECT COUNT(janitor_id) FROM janitor_image WHERE janitor_image.janitor_id = :id", nativeQuery = true)
    Long count(Long id);

    @Override
    void delete(JanitorImage janitorImage);

    @Override
    void deleteById(Long aLong);
    @Query(value = "SELECT janitor_image.janitor_id from janitor_image WHERE janitor_image.id = :id", nativeQuery = true)
    Long findJanitorIdByImageId(Long id);

}
