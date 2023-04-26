package com.skypro.telegram_team.repositories;

import com.skypro.telegram_team.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<Photo> findPhotoByAnimalId(Long animalId);
}
