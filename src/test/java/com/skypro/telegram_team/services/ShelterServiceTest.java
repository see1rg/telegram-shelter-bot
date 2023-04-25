package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.ShelterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {
    @InjectMocks
    private ShelterService shelterService;
    @Mock
    private ShelterRepository shelterRepository;
    @Mock
    private AnimalRepository animalRepository;
    private Shelter expectedShelter;

    @BeforeEach
    public void setup() {
        expectedShelter = new Shelter();
        expectedShelter.setId(1L);
        expectedShelter.setName("приют для собак");
        expectedShelter.setType(Animal.TypeAnimal.CAT);
    }

    @Test
    public void findById() {
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(expectedShelter));
        Shelter actualShelter = shelterService.findById(expectedShelter.getId());
        Assertions.assertEquals(expectedShelter, actualShelter);
        verify(shelterRepository, times(1)).findById(any());
    }

    @Test
    public void findAll() {
        List<Shelter> expectedShelters = List.of(expectedShelter);
        when(shelterRepository.findAll()).thenReturn(expectedShelters);
        Collection<Shelter> actualShelters = shelterService.findAll();
        Assertions.assertEquals(expectedShelters, actualShelters);
        verify(shelterRepository, times(1)).findAll();
    }

    @Test
    public void create() {
        when(shelterRepository.save(any())).thenReturn(expectedShelter);
        Shelter actualShelter = shelterService.create(expectedShelter, expectedShelter.getType());
        Assertions.assertEquals(expectedShelter, actualShelter);
        verify(shelterRepository, times(1)).save(any());

    }

    @Test
    public void update() {
        when(shelterRepository.save(any())).thenReturn(expectedShelter);
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(expectedShelter));
        Shelter updatedShelter = new Shelter();
        updatedShelter.setName("приют для собак");
        Shelter actualShelter = shelterService.update(updatedShelter, expectedShelter.getId());
        Assertions.assertEquals(expectedShelter.getId(), actualShelter.getId());
        Assertions.assertEquals(updatedShelter.getName(), actualShelter.getName());
        verify(shelterRepository, times(1)).findById(any());
        verify(shelterRepository, times(1)).save(any());
    }

    @Test
    public void delete() {
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(expectedShelter));
        Shelter actualShelter = shelterService.delete(expectedShelter.getId());
        Shelter shelter = mock(Shelter.class);
        Assertions.assertEquals(expectedShelter, actualShelter);
        verify(shelterRepository, times(1)).findById(any());
    }

    @Test
    public void assignAnimalsToShelters() {
        Animal animal = new Animal();
        animal.setId(99L);
        animal.setName("barsik");
        animal.setType(Animal.TypeAnimal.CAT);
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(expectedShelter));
        when(animalRepository.save(any())).thenReturn(animal);
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        shelterService.assignAnimalsToShelters(expectedShelter.getId(), animal.getId());
        Assertions.assertEquals(animal.getShelter().getId(), expectedShelter.getId());
        verify(shelterRepository, times(1)).findById(any());
        verify(animalRepository, times(1)).save(any());
        verify(animalRepository, times(1)).findById(any());
    }

    @Test
    public void shouldThrowsIllegalStateExceptionWhenMethodAssignAnimalsToSheltersRuns() {
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setType(Animal.TypeAnimal.DOG);
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(expectedShelter));
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        assertThrows(IllegalStateException.class, () -> shelterService
                .assignAnimalsToShelters(expectedShelter.getId(), animal.getId()));
    }

}
