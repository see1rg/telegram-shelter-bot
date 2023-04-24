package com.skypro.telegram_team.services;


import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Cat;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @InjectMocks
    private AnimalService animalService;
    @Mock
    private AnimalRepository animalRepository;
    private Animal expectedAnimal;

    @BeforeEach
    public void setup() {
        expectedAnimal = new Cat();
        expectedAnimal.setId(1L);
        expectedAnimal.setName("sharik");
        expectedAnimal.setState(Animal.AnimalStateEnum.IN_TEST);
        User user = new User();
        user.setId(1L);
        user.setState(User.OwnerStateEnum.PROBATION);
        expectedAnimal.setUser(user);

    }

    @Test
    public void createAnimal() {
        when(animalRepository.save(any())).thenReturn(expectedAnimal);
        Animal actualAnimal = animalService.create(expectedAnimal);
        assertEquals(expectedAnimal, actualAnimal);
    }

    @Test
    public void findById() {
        when(animalRepository.findById(any())).thenReturn(Optional.ofNullable(expectedAnimal));
        Animal actualAnimal = animalService.findById(expectedAnimal.getId());
        assertEquals(expectedAnimal, actualAnimal);
        assertEquals(expectedAnimal.getId(), actualAnimal.getId());
    }

    @Test
    public void deleteById() {
        when(animalRepository.findById(any())).thenReturn(Optional.ofNullable(expectedAnimal));
        Animal actualAnimal = animalService.deleteById(expectedAnimal.getId());
        assertEquals(expectedAnimal, actualAnimal);
    }

    @Test
    public void updateAnimal() {
        Animal animalInDB = new Dog();
        animalInDB.setName("sharik");
        animalInDB.setId(1L);
        Animal updatedAnimal = new Cat();
        updatedAnimal.setName("pushok");
        when(animalRepository.findById(any())).thenReturn(Optional.of(animalInDB));
        when(animalRepository.save(any())).thenReturn(updatedAnimal);
        Animal actualAnimal = animalService.update(updatedAnimal, animalInDB.getId());
        assertEquals(actualAnimal.getId(), animalInDB.getId());
        assertEquals(actualAnimal.getName(), updatedAnimal.getName());
    }

    //не протестирована сортировка по имени
    @Test
    public void findAll() {
        Animal animal1 = new Dog();
        animal1.setName("рекс");
        Animal animal2 = new Dog();
        animal2.setName("хатико");
        Animal animal3 = new Dog();
        animal3.setName("бетховен");
        when(animalRepository.findAll(Sort.by("name"))).thenReturn(List.of(animal1, animal2, animal3));
        List<Animal> allAnimals = animalService.findAll();
        assertTrue(allAnimals.size() != 0);
    }

    @Test
    public void findByName() {
        List<Animal> expectedAnimals = List.of(expectedAnimal);
        when(animalRepository.findAnimalsByName(any())).thenReturn(Optional.of(expectedAnimals));
        List<Animal> actualAnimals = animalService.findByName(expectedAnimal.getName());
        assertEquals(expectedAnimals, actualAnimals);
    }

    @Test
    public void findByUserId() {
        when(animalRepository.findAnimalsByUserId(any())).thenReturn(expectedAnimal);
        Animal actualAnimal = animalService.findByUserId(expectedAnimal.getUser().getId());
        assertEquals(expectedAnimal, actualAnimal);
    }

    @Test
    public void findAllByUserIdNotNullAndState() {
        List<Animal> expectedAnimals = List.of(expectedAnimal);
        when(animalRepository.findAllByUserIdNotNullAndState(any())).thenReturn(expectedAnimals);
        List<Animal> actualAnimals = animalService.findAllByUserIdNotNullAndState(expectedAnimal.getState());
        assertEquals(expectedAnimals, actualAnimals);
    }

    @Test
    public void findByUserState() {
        List<Animal> expectedAnimals = List.of(expectedAnimal);
        when(animalRepository.findByUserContainsOrderByState(any())).thenReturn(expectedAnimals);
        List<Animal> actualAnimals = animalService.findByUserState(expectedAnimal.getUser().getState());
        assertEquals(expectedAnimals, actualAnimals);
    }

}
