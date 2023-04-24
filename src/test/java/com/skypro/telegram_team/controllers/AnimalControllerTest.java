package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Cat;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.ShelterService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private AnimalController animalController;
    @SpyBean
    private AnimalService animalService;
    @MockBean
    private ShelterController shelterController;
    @MockBean
    ReportController reportController;

    @MockBean
    UserController userController;
    @MockBean
    private AnimalRepository animalRepository;
    private final Animal animal = new Animal();
    private final JSONObject jsonAnimal = new JSONObject();

    @BeforeEach
    public void setup() throws Exception {
        animal.setName("barsik");
        animal.setId(2L);
        animal.setBreed("metis");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        jsonAnimal.put("name", animal.getName());
        jsonAnimal.put("id", animal.getId());
        jsonAnimal.put("breed", animal.getBreed());
        jsonAnimal.put("state", animal.getState());
    }

    @Test
    public void findAnimalById() throws Exception{
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/" + animal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()));
    }

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
