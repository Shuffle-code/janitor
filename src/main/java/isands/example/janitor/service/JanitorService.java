package isands.example.janitor.service;

import isands.example.janitor.dao.JanitorDao;
import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.JanitorImage;
import isands.example.janitor.entity.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class JanitorService {
    private final JanitorDao janitorDao;
    private final JanitorImageService janitorImageService;
    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long count() {
        return janitorDao.count();
    }

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Integer countPlaying() {
        return janitorDao.findAllByStatus(Status.ACTIVE).size();
    }

    public Janitor save(Janitor janitor, File file) {
        if (janitor.getId() != null) {
            Optional<Janitor> janitorFromDBOptional = janitorDao.findById(janitor.getId());
            if (janitorFromDBOptional.isPresent()) {
                Janitor janitorFromDB = janitorFromDBOptional.get();
                janitorFromDB.setFirstname(janitor.getFirstname());
                janitorFromDB.setPatronymic(janitor.getPatronymic());
                janitorFromDB.setYearOfBirth(janitor.getYearOfBirth());
                janitorFromDB.setStatus(janitor.getStatus());
                return janitorDao.save(janitorFromDB);
            }
        }
        return janitorDao.save(janitor);
    }


    public Long maxId(){
        return janitorDao.maxId();
    }

//    @Transactional
    public Janitor save(Janitor janitor, MultipartFile multipartFile) {
        if (janitor.getId() != null) {
            janitorDao.findById(janitor.getId()).ifPresent(
                    (p) -> janitor.setVersion(p.getVersion())
            );
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String pathToSavedFile = janitorImageService.save(multipartFile);
            JanitorImage janitorImage = JanitorImage.builder()
                    .path(pathToSavedFile)
                    .janitor(janitor)
                    .build();
            janitor.addImage(janitorImage);
        }
        return janitorDao.save(janitor);
    }


    @Transactional
    public Janitor save(final Janitor janitor) {
        return save(janitor, (MultipartFile) null);
    }


    public List<Janitor> findAll() {
        return janitorDao.findAll();
    }

    public List<Janitor> findAllActive() {
        return janitorDao.findAllByStatus(Status.ACTIVE);
    }

    public void deleteById(Long id) {
        try {
            janitorDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error(e.getMessage());
        }
    }

    public void statusDelete(Long id) {
        Optional<Janitor> Janitor = janitorDao.findById(id);
        Janitor.ifPresent(p -> {
            p.setStatus(Status.DELETED);
            janitorDao.save(p);
        });
    }

    public void disable(Long id) {
        Optional<Janitor> Janitor = janitorDao.findById(id);
        Janitor.ifPresent(p -> {
            p.setStatus(Status.DISABLE);
            janitorDao.save(p);
        });
    }
    public List<Janitor> findAll(int page, int size) {
        return janitorDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size));
    }
    @Transactional(readOnly = true)
    public List<Janitor> findAllActiveSortedById() {
        return janitorDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC,"id"));
    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllActiveSortedByRating() {
//        return janitorDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC,"rating"));
//    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllDisableSortedByRating() {
//        return janitorDao.findAllByStatus(Status.DISABLE, Sort.by(Sort.Direction.DESC,"rating"));
//    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllNotActiveSortedByRating() {
//        return janitorDao.findAllByStatus(Status.NOT_ACTIVE, Sort.by(Sort.Direction.DESC,"rating"));
//    }

    public List<Janitor> addListForMainPage(){
        List<Janitor> janitors = Stream
                .of( findAll())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return janitors;
    }

    @Transactional(readOnly = true)
    public List<Janitor> findAllSortedById(int page, int size) {
        return janitorDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size, Sort.by("id")));
    }


    public String stringJanitor(Janitor janitor){
        String str = janitor.getFirstname() + janitor.getLastname();
        return str;
    }
    @Transactional(readOnly = true)
    public Janitor findById(Long id) {
    return janitorDao.findById(id).orElse(null);
}

}
