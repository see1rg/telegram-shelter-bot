package com.skypro.telegram_team.repositories;

import com.skypro.telegram_team.models.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findAnimalsByUserId(Long userId);

    Optional<Animal> findByName(String name);

    <Animal>  findAll(Class<Animal> animalClass);
}
