package isands.example.janitor.service;

import isands.example.janitor.dao.JanitorDao;
import isands.example.janitor.dao.JanitorImageDao;
import isands.example.janitor.entity.Janitor;
import isands.example.janitor.entity.JanitorImage;
import isands.example.janitor.exception.StorageException;
import isands.example.janitor.exception.StorageFileNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JanitorImageService {
    private static final String path = "Janitors";
    private final String startImage = "image104-66.jpg";

    @Value("${storage.location}")
    private String storagePath;

    private final JanitorImageDao janitorImageDao;
    private final JanitorDao janitorDao;
    private Path rootLocation;

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long count() {
        return janitorImageDao.count();
    }

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long countImagesOfJanitor(Long id) {
        return janitorImageDao.count(id);
    }

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storagePath);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("Error while creating storage {}", rootLocation.toAbsolutePath());
            throw new StorageException(String.format("Error while creating storage %s", rootLocation.toAbsolutePath()));
        }
    }

    public String save(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        return this.save(file, filename);
    }

    public String save(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException(String.format("File %s is empty", filename));
            }
            if (filename.contains("..")) {
                throw new StorageException(String.format("Symbol '..' do not permit"));
            }
            Files.createDirectories(rootLocation.resolve(path));
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootLocation.resolve(path))) {
                for (Path child : dirStream) {
                    if (child.getFileName().toString().equals(filename)) {
                        throw new StorageException(String.format("File with name %s/%s already exists", rootLocation.resolve(path), filename));
                    }
                }
            } catch (IOException e) {
                throw new StorageException(String.format("Error while creating file %s", filename));
            }

        } catch (IOException e) {
            throw new StorageException("Error while creating storage");
        }
        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(path).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(String.format("Error while saving file %s", filename));
        }
        return filename;
    }

    public Janitor saveJanitorImage(Long janitorId, MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            Janitor janitor = janitorDao.getReferenceById(janitorId);
            String pathToSavedFile = save(multipartFile);
            JanitorImage janitorImage = JanitorImage.builder()
                    .path(pathToSavedFile)
                    .janitor(janitor)
                    .build();
            janitor.addImage(janitorImage);
            Janitor savePlayer = janitorDao.save(janitor);
            deleteStartImage(janitorImage);
            return savePlayer;
        }
        return null;
    }

    public void deleteStartImage(JanitorImage janitorImage){
        Long idJanitor = janitorImage.getJanitor().getId();
        JanitorImage image = janitorImageDao.findFirstByJanitorId(idJanitor);
        if (janitorImageDao.count(janitorImage.getJanitor().getId()) > 1 && image.getPath().equals("image104-66.jpg")){
//            log.info(image.getPath());
            janitorImageDao.delete(image);
        }
    }
    public void deleteImage(Long id){
        janitorImageDao.deleteById(id);
    }
    public BufferedImage loadFileAsImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByJanitorId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public BufferedImage loadFileAsImageByIdImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByImageId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public String uploadMultipleFilesByJanitorId(Long id) {
        return janitorImageDao.findImageNameByJanitorId(id);
    }
    public String uploadMultipleFilesByImageId(Long id) {
        return janitorImageDao.findImageNameByImageId(id);
    }
    public List<Long> uploadMultipleFiles(Long id) {
        return janitorImageDao.findAllIdImagesByJanitorId(id);
    }

    public Long getJanitorIdByImageId(Long id){
        return janitorImageDao.findJanitorIdByImageId(id);
    }

    public Resource loadAsResource(String filename) {
        if (StringUtils.hasText(filename)) {
            try {
                Path file = rootLocation.resolve(path).resolve(filename);
//                private static final String path = "products";
//                8e6d4478-ee77-4d43-96ef-0d6df9fb1589_i.jpg
//                products/8e6d4478-ee77-4d43-96ef-0d6df9fb1589_i.jpg
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new StorageFileNotFoundException(String.format("File %s not found in directory %s", filename, path));
                }
            } catch (MalformedURLException e) {
                throw new StorageFileNotFoundException(String.format("File %s not found in directory %s", filename, path), e);
            }
        } else {
            throw new StorageFileNotFoundException(String.format("Filename cannot be empty: %s", filename));
        }
    }

    public void addStartImage(Janitor janitor){
        JanitorImage janitorImage = new JanitorImage();
        janitorImage.setPath(startImage);
        janitorImage.setJanitor(janitor);
        janitorImageDao.save(janitorImage);
    }
}
