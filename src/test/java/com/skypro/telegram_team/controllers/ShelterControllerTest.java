package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.repositories.ShelterRepository;
import com.skypro.telegram_team.services.ShelterService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ShelterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ShelterController shelterController;
    @MockBean
    private AnimalController animalController;
    @MockBean
    private UserController userController;
    @MockBean
    private ReportController reportController;
    @SpyBean
    private ShelterService shelterService;
    @MockBean
    private ShelterRepository shelterRepository;
    private Shelter shelter;
    private JSONObject jsonShelter;

    @BeforeEach
    public void setup() throws Exception {
        shelter = new Shelter();
        shelter.setId(1L);
        shelter.setType(Shelter.Type.DOGS);
        jsonShelter = new JSONObject();
        jsonShelter.put("id", shelter.getId());
        jsonShelter.put("type", shelter.getType());

    }

    @Test
    public void findById() throws Exception {
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(shelter));
        mockMvc.perform(MockMvcRequestBuilders.get("/shelters/" + shelter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shelter.getId()))
                .andExpect(jsonPath("$.type").value(shelter.getType().toString()));
    }

    @Test
    public void findAll() throws Exception {
        when(shelterRepository.findAll()).thenReturn(List.of(shelter));
        mockMvc.perform(MockMvcRequestBuilders.get("/shelters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(shelter.getId()))
                .andExpect(jsonPath("$[0].type").value(shelter.getType().toString()));
    }

    @Test
    public void create() throws Exception {
        when(shelterRepository.save(any())).thenReturn(shelter);
        mockMvc.perform(MockMvcRequestBuilders.post("/shelters")
                        .content(jsonShelter.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shelter.getId()))
                .andExpect(jsonPath("$.type").value(shelter.getType().toString()));
    }

    @Test
    public void delete() throws Exception {
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(shelter));
        mockMvc.perform(MockMvcRequestBuilders.delete("/shelters/" + shelter.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void update() throws Exception {
        when(shelterRepository.findById(any())).thenReturn(Optional.ofNullable(shelter));
        when(shelterRepository.save(any())).thenReturn(shelter);
        mockMvc.perform(MockMvcRequestBuilders.put("/shelters/" + shelter.getId())
                        .content(jsonShelter.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shelter.getId()))
                .andExpect(jsonPath("$.type").value(shelter.getType().toString()));
    }
}
