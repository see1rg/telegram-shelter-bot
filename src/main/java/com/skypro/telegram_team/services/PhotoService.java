package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Photo;
import com.skypro.telegram_team.repositories.PhotoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Log4j2
@Service
@Transactional
public class PhotoService {
    @Value("${photo.dir.path}")
    private String photosDir;

    private final PhotoRepository photoRepository;
    private final AnimalService animalService;

    public PhotoService(PhotoRepository photoRepository, AnimalService animalService) {
        this.photoRepository = photoRepository;
        this.animalService = animalService;
    }

    public void uploadPhoto(Long animalId, MultipartFile file) throws IOException {
        log.debug("Requesting to upload the photo for the animal with id: {}.", animalId);

        Animal animal = animalService.findById(animalId);

        Path filePath = Path.of(photosDir, animalId + "." + getExtension(Objects.requireNonNull(file.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)

        ) {
            bis.transferTo(bos);
        }
        Photo photo = findPhotoByAnimalId(animalId);
        photo.setAnimal(animal);
        photo.setFilePath(filePath.toString());
        photo.setFileSize(file.getSize());
        photo.setMediaType(file.getContentType());
        photo.setPreview(generatedImagePreview(filePath));

        photoRepository.save(photo);
    }

    private byte[] generatedImagePreview(Path filePath) throws IOException {
        log.info("Generating preview.");

        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public Photo findPhotoByAnimalId(Long animalId) {
        log.info("Requesting to find the avatar by student id: {}.", animalId);
        return photoRepository.findPhotoByAnimalId(animalId)
                .orElse(new Photo());
    }

    private String getExtension(String fileName) {
        log.info("Getting extension for the file with name: {}.", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
