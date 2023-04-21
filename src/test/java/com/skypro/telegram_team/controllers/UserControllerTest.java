package com.skypro.telegram_team;

import com.skypro.telegram_team.controllers.AnimalController;
import com.skypro.telegram_team.controllers.ReportController;
import com.skypro.telegram_team.controllers.UserController;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.UserService;
import org.json.JSONException;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AnimalController animalController;
    @MockBean
    private ReportController reportController;
    @InjectMocks
    private UserController userController;
    @MockBean
    private UserRepository userRepository;
    @SpyBean
    private UserService userService;
    @SpyBean
    private AnimalService animalService;
    @MockBean
    private AnimalRepository animalRepository;
    private final User user = new User();
    private final JSONObject jsonUser = new JSONObject();
    private final Animal animal = new Dog();

    @BeforeEach
    public void setup() throws JSONException {
        animal.setId(1L);
        user.setId(1L);
        user.setTelegramId(1L);
        user.setName("dima");
        user.setPhone("89991717272");
        user.setEmail("123@gmail.com");
        user.setVolunteer(true);
        user.setState(User.OwnerStateEnum.PROBATION);
        jsonUser.put("id", user.getId());
        jsonUser.put("telegramId", user.getTelegramId());
        jsonUser.put("name", user.getName());
        jsonUser.put("phone", user.getPhone());
        jsonUser.put("email", user.getEmail());
        jsonUser.put("state", user.getState());
        jsonUser.put("isVolunteer", user.isVolunteer());
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
    }

    @Test
    public void createUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(jsonUser.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.telegramId").value(user.getTelegramId()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.state").value(user.getState().toString()));
    }

    @Test
    public void updateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId())
                        .content(jsonUser.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.telegramId").value(user.getTelegramId()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.state").value(user.getState().toString()));
    }

    @Test
    public void findById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.telegramId").value(user.getTelegramId()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.state").value(user.getState().toString()));
    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].telegramId").value(user.getTelegramId()))
                .andExpect(jsonPath("$[0].phone").value(user.getPhone()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].state").value(user.getState().toString()));
    }

    @Test
    public void deleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void userIsVolunteer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/" + user.getId() + "/volunteer?isVolunteer=" + user.isVolunteer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.telegramId").value(user.getTelegramId()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.state").value(user.getState().toString()));
    }

    @Test
    public void joinAnimalAndUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/join?animalId=" + animal.getId() + "&userId=" + user.getId()))
                .andExpect(status().isOk());
    }


    @Test
    public void updateState() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + user.getId()
                        + "/state?state=" + user.getState() + "&daysForTest=" + user.getDaysForTest()))
                .andExpect(status().isOk());
    }

}
