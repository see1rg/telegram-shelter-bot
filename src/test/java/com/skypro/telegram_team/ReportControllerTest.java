package com.skypro.telegram_team;

import com.skypro.telegram_team.controllers.AnimalController;
import com.skypro.telegram_team.controllers.ReportController;
import com.skypro.telegram_team.controllers.UserController;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.ReportRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import com.skypro.telegram_team.services.ReportService;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private ReportController reportController;
    @MockBean
    private AnimalController animalController;
    @MockBean
    private UserController userController;
    @SpyBean
    private ReportService reportService;
    @MockBean
    private ReportRepository reportRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AnimalRepository animalRepository;
    private final Report report = new Report();
    private final JSONObject jsonReport = new JSONObject();
    private final JSONObject jsonAnimal = new JSONObject();
    private final JSONObject jsonUser = new JSONObject();
    private final User user = new User();
    private final Animal animal = new Dog();

    @BeforeEach
    public void setup() throws Exception {
        //user
        user.setState(User.OwnerStateEnum.PROBATION);
        user.setTelegramId(1L);
        user.setId(1L);
        //animal
        animal.setId(1L);
        animal.setName("sharik");
        animal.setState(Animal.AnimalStateEnum.IN_SHELTER);
        //report
        report.setId(1L);
        report.setDiet("diet");
        report.setChangeBehavior("behavior");
        report.setWellBeing("health");
        report.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        report.setUser(user);
        report.setAnimal(animal);
        //animaljson
        jsonAnimal.put("id", report.getAnimal().getId());
        jsonAnimal.put("name", report.getAnimal().getName());
        jsonAnimal.put("state", report.getAnimal().getState());
        //userjson
        jsonUser.put("id", report.getUser().getId());
        jsonUser.put("telegramId", report.getUser().getTelegramId());
        jsonUser.put("state", report.getUser().getState());
        //reportjson
        jsonReport.put("id", report.getId());
        jsonReport.put("diet", report.getDiet());
        jsonReport.put("changeBehavior", report.getChangeBehavior());
        jsonReport.put("wellBeing", report.getWellBeing());
        jsonReport.put("date", report.getDate());
        jsonReport.put("user", jsonUser);
        jsonReport.put("animal", jsonAnimal);
        //when
        when(reportRepository.findById(any())).thenReturn(Optional.of(report));
        when(reportRepository.findAll()).thenReturn(List.of(report));
        when(reportRepository.save(any())).thenReturn(report);
    }

//    @Test
//    public void findReportById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/reports/" + report.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.diet").value(report.getDiet()))
//                .andExpect(jsonPath("$.id").value(report.getId()))
//                .andExpect(jsonPath("$.wellBeing").value(report.getWellBeing()))
//                .andExpect(jsonPath("$.date").value(report.getDate().toString()))
//                .andExpect(jsonPath("$.changeBehavior").value(report.getChangeBehavior()))
//                .andExpect(jsonPath("$.user.id").value(report.getUser().getId()))
//                .andExpect(jsonPath("$.user.telegramId").value(report.getUser().getTelegramId()))
//                .andExpect(jsonPath("$.user.state").value(report.getUser().getState().toString()))
//                .andExpect(jsonPath("$.animal.id").value(report.getAnimal().getId()))
//                .andExpect(jsonPath("$.animal.name").value(report.getAnimal().getName()))
//                .andExpect(jsonPath("$.animal.state").value(report.getAnimal().getState().toString()));
//    }

    @Test
    public void deleteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reports/" + report.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diet").value(report.getDiet()))
                .andExpect(jsonPath("$[0].id").value(report.getId()))
                .andExpect(jsonPath("$[0].wellBeing").value(report.getWellBeing()))
                .andExpect(jsonPath("$[0].date").value(report.getDate().toString()))
                .andExpect(jsonPath("$[0].changeBehavior").value(report.getChangeBehavior()))
                .andExpect(jsonPath("$[0].user.id").value(report.getUser().getId()))
                .andExpect(jsonPath("$[0].user.telegramId").value(report.getUser().getTelegramId()))
                .andExpect(jsonPath("$[0].user.state").value(report.getUser().getState().toString()))
                .andExpect(jsonPath("$[0].animal.id").value(report.getAnimal().getId()))
                .andExpect(jsonPath("$[0].animal.name").value(report.getAnimal().getName()))
                .andExpect(jsonPath("$[0].animal.state").value(report.getAnimal().getState().toString()));
    }

//    @Test
//    public void createReport() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/reports")
//                        .content(jsonReport.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.diet").value(report.getDiet()))
//                .andExpect(jsonPath("$.id").value(report.getId()))
//                .andExpect(jsonPath("$.wellBeing").value(report.getWellBeing()))
//                .andExpect(jsonPath("$.date").value(report.getDate().toString()))
//                .andExpect(jsonPath("$.changeBehavior").value(report.getChangeBehavior()))
//                .andExpect(jsonPath("$.user.id").value(report.getUser().getId()))
//                .andExpect(jsonPath("$.user.telegramId").value(report.getUser().getTelegramId()))
//                .andExpect(jsonPath("$.user.state").value(report.getUser().getState().toString()))
//                .andExpect(jsonPath("$.animal.id").value(report.getAnimal().getId()))
//                .andExpect(jsonPath("$.animal.name").value(report.getAnimal().getName()))
//                .andExpect(jsonPath("$.animal.state").value(report.getAnimal().getState().toString()));
//    }

//    @Test
//    public void updateReport() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.put("/reports/" + report.getId())
//                        .content(jsonReport.toString())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.diet").value(report.getDiet()))
//                .andExpect(jsonPath("$.id").value(report.getId()))
//                .andExpect(jsonPath("$.wellBeing").value(report.getWellBeing()))
//                .andExpect(jsonPath("$.date").value(report.getDate().toString()))
//                .andExpect(jsonPath("$.changeBehavior").value(report.getChangeBehavior()))
//                .andExpect(jsonPath("$.user.id").value(report.getUser().getId()))
//                .andExpect(jsonPath("$.user.telegramId").value(report.getUser().getTelegramId()))
//                .andExpect(jsonPath("$.user.state").value(report.getUser().getState().toString()))
//                .andExpect(jsonPath("$.animal.id").value(report.getAnimal().getId()))
//                .andExpect(jsonPath("$.animal.name").value(report.getAnimal().getName()))
//                .andExpect(jsonPath("$.animal.state").value(report.getAnimal().getState().toString()));
//    }
}
