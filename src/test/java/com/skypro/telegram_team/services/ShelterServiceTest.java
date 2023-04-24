package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Shelter;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {
    @InjectMocks
    private ShelterService shelterService;
    @Mock
    private ShelterRepository shelterRepository;
    private Shelter expectedShelter;

    @BeforeEach
    public void setup() {
        expectedShelter = new Shelter();
        expectedShelter.setId(1L);
        expectedShelter.setName("приют для собак");
        expectedShelter.setType(Shelter.Type.DOGS);
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
        Shelter actualShelter = shelterService.create(expectedShelter);
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

}
