package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.services.AnimalService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private ReportController reportController;
    @MockBean
    private UserController userController;
    @MockBean
    private AnimalRepository animalRepository;
    private final Animal animal = new Animal();
    private final JSONObject jsonAnimal = new JSONObject();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        animal.setName("barsik");
        animal.setId(1L);
        animal.setBreed("metis");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        animal.setType(Animal.TypeAnimal.CAT);
        jsonAnimal.put("id", animal.getId());
        jsonAnimal.put("name", animal.getName());
        jsonAnimal.put("breed", animal.getBreed());
        jsonAnimal.put("state", animal.getState());
        jsonAnimal.put("type", animal.getType());
        when(animalRepository.save(any())).thenReturn(animal);
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        when(animalRepository.findAnimalsByName(any())).thenReturn(Optional.of(List.of(animal)));
        when(animalRepository.findAll(Sort.by("name"))).thenReturn(List.of(animal));
    }

    @Test
    public void createAnimal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/animals?type=" + animal.getType())
                        .content(jsonAnimal.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()))
                .andExpect(jsonPath("$.type").value(animal.getType().toString()));
    }

    @Test
    public void findAnimalById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/" + animal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()))
                .andExpect(jsonPath("$.type").value(animal.getType().toString()));
    }

    @Test
    public void updateAnimal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/animals/" + animal.getId())
                        .content(jsonAnimal.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(animal.getName()))
                .andExpect(jsonPath("$.id").value(animal.getId()))
                .andExpect(jsonPath("$.breed").value(animal.getBreed()))
                .andExpect(jsonPath("$.state").value(animal.getState().toString()))
                .andExpect(jsonPath("$.type").value(animal.getType().toString()));
    }

    @Test
    public void deleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/animals/" + animal.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void findByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/name/").param("name", "sharik"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()))
                .andExpect(jsonPath("$[0].type").value(animal.getType().toString()));
    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(animal.getName()))
                .andExpect(jsonPath("$[0].id").value(animal.getId()))
                .andExpect(jsonPath("$[0].breed").value(animal.getBreed()))
                .andExpect(jsonPath("$[0].state").value(animal.getState().toString()))
                .andExpect(jsonPath("$[0].type").value(animal.getType().toString()));
    }

    @Test
    public void photoUpload() throws Exception {
        //Given
        Resource resource = new ClassPathResource("photo/cat.jpeg");
        MockMultipartFile file = new MockMultipartFile(
                "photo",
                "cat.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(resource.getFile().toPath())
        );
        //Then
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/animals/1/photo").file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void photoDownload() throws Exception {
        //Given
        Resource resource = new ClassPathResource("photo/cat.jpeg");
        byte[] photo = Files.readAllBytes(resource.getFile().toPath());
        Animal expected = new Animal();
        expected.setId(1L);
        expected.setPhoto(photo);
        //When
        when(animalRepository.findById(1L)).thenReturn(Optional.of(expected));
        //Then
        mockMvc.perform(MockMvcRequestBuilders.get("/animals/1/photo"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected.getPhoto()));
    }
}
