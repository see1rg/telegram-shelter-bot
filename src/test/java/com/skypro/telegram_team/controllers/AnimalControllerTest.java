package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.controllers.AnimalController;
import com.skypro.telegram_team.controllers.ReportController;
import com.skypro.telegram_team.controllers.UserController;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.services.AnimalService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
    ReportController reportController;

    @MockBean
    UserController userController;
    @MockBean
    private AnimalRepository animalRepository;

    private final Animal animal = new Animal();

    private final JSONObject jsonAnimal = new JSONObject();

    @BeforeEach
    public void setup() throws Exception {
        animal.setName("sharik");
        animal.setId(1L);
        animal.setBreed("metis");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        jsonAnimal.put("name", animal.getName());
        jsonAnimal.put("id", animal.getId());
        jsonAnimal.put("breed", animal.getBreed());
        jsonAnimal.put("state", animal.getState());
        Mockito.when(animalRepository.save(any())).thenReturn(animal);
        Mockito.when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        Mockito.when(animalRepository.findAnimalsByName(any())).thenReturn(Optional.of(List.of(animal)));
        Mockito.when(animalRepository.findAll(Sort.by("name"))).thenReturn(List.of(animal));
    }

    @Test
    public void createAnimal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/animals")
                        .content(jsonAnimal.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()));

    }

    @Test
    public void findAnimalById() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/" + animal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()));
    }

    @Test
    public void updateById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/animals/" + animal.getId())
                .content(jsonAnimal.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()));
    }

    @Test
    public void deleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/animals/" + animal.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void findByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/animals?name=" + animal.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()));
    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()));
    }

}
