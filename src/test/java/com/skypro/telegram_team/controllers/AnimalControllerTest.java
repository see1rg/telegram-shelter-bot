package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.ShelterRepository;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.ShelterService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private AnimalController animalController;
    @SpyBean
    private AnimalService animalService;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private ShelterController shelterController;
    @MockBean
    private ReportController reportController;
    @MockBean
    private UserController userController;
    @MockBean
    private AnimalRepository animalRepository;
    @MockBean
    private ShelterRepository shelterRepository;
    @Mock
    private final Animal animal = new Animal();
    private final JSONObject jsonAnimal = new JSONObject();
    private final JSONObject jsonShelter = new JSONObject();
    private final Shelter shelter = new Shelter();

    @BeforeEach
    public void setup() throws Exception {
        shelter.setType(Animal.TypeAnimal.CAT);
        shelter.setName("приют для кошек");
        shelter.setId(1L);
        animal.setName("barsik");
        animal.setId(1L);
        animal.setBreed("metis");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        animal.setType(Animal.TypeAnimal.CAT);
        animal.setType(Animal.TypeAnimal.DOG);
        animal.setShelter(shelter);
        jsonAnimal.put("id", animal.getId());
        jsonAnimal.put("name", animal.getName());
        jsonAnimal.put("breed", animal.getBreed());
        jsonAnimal.put("state", animal.getState());
        jsonAnimal.put("type", animal.getType());
    }
// не работает
//    @Test
//    public void createAnimal() throws Exception {
//        when(animalRepository.save(any())).thenReturn(animal);
//        mockMvc.perform(MockMvcRequestBuilders.post("/animals?type=" + animal.getType())
//                        .content(jsonAnimal.toString())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(animal.getName()))
//                .andExpect(jsonPath("$.id").value(animal.getId()))
//                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
//                .andExpect(jsonPath("$.state").value(animal.getState().toString()))
//                .andExpect(jsonPath("$.type").value(animal.getType().toString()));
//    }

    @Test
    public void findAnimalById() throws Exception {
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/" + animal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()));
    }

// Не работает
//    @Test
//    public void updateAnimal() throws Exception {
//        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
//        when(animalRepository.save(any())).thenReturn(animal);
//        mockMvc.perform(MockMvcRequestBuilders.put("/animals/" + animal.getId())
//                        .content(jsonAnimal.toString())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(animal.getName()))
//                .andExpect(jsonPath("$.id").value(animal.getId()))
//                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
//                .andExpect(jsonPath("$.state").value(animal.getState().toString()));
//    }

    @Test
    public void deleteById() throws Exception {
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        mockMvc.perform(MockMvcRequestBuilders.delete("/animals/" + animal.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void findByName() throws Exception {
        when(animalRepository.findAnimalsByName(any())).thenReturn(Optional.of(List.of(animal)));
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/name/").param("name", "sharik"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()));
    }

    @Test
    public void findAll() throws Exception {
        when(animalRepository.findAll(Sort.by("name"))).thenReturn(List.of(animal));
        mockMvc.perform(MockMvcRequestBuilders.get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()));
    }

}
