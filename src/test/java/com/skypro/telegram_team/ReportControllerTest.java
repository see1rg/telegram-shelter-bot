package com.skypro.telegram_team;

import com.skypro.telegram_team.controllers.AnimalController;
import com.skypro.telegram_team.controllers.ReportController;
import com.skypro.telegram_team.controllers.UserController;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.ReportRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import com.skypro.telegram_team.services.ReportService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AnimalController animalController;
    @SpyBean
    private ReportService reportService;
    @InjectMocks
    private ReportController reportController;
    @MockBean
    private UserController userController;
    @MockBean
    private ReportRepository reportRepository;
    @MockBean
    private UserRepository userRepository;
    private final Report report = new Report();
    private final JSONObject jsonReport = new JSONObject();
    private final User user = new User();
    private final Animal animal = new Animal();

    @BeforeEach
    public void setup() throws Exception {
        user.setState(User.OwnerStateEnum.PROBATION);
        user.setTelegramId(1L);
        user.setId(1L);
        animal.setId(1L);
        animal.setName("sharik");
        report.setId(1L);
        report.setDiet("diet");
        report.setChangeBehavior("behavior");
        report.setWellBeing("health");
        report.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        report.setUser(user);
        report.setAnimal(animal);
        jsonReport.put("id", report.getId());
        jsonReport.put("diet", report.getDiet());
        jsonReport.put("changeBehavior", report.getChangeBehavior());
        jsonReport.put("wellBeing", report.getWellBeing());
        jsonReport.put("date", report.getDate());
        jsonReport.put("user", report.getUser());
        jsonReport.put("animal", report.getAnimal());
    }

    @Test
    public void findReportById() throws Exception {
        Mockito.when(reportRepository.findById(any())).thenReturn(Optional.of(report));
        mockMvc.perform(MockMvcRequestBuilders.get("/reports/" + report.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diet").value(report.getDiet()))
                .andExpect(jsonPath("$.id").value(report.getId()))
                .andExpect(jsonPath("$.wellBeing").value(report.getWellBeing()))
                .andExpect(jsonPath("$.date").value(report.getDate().toString()))
                .andExpect(jsonPath("$.changeBehavior").value(report.getChangeBehavior()));
    }

    @Test
    public void deleteById() throws Exception {
        Mockito.when(reportRepository.findById(any())).thenReturn(Optional.of(report));
        mockMvc.perform(MockMvcRequestBuilders.delete("/reports/" + report.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void findAll() throws Exception {
        Mockito.when(reportRepository.findAll()).thenReturn(List.of(report));
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diet").value(report.getDiet()))
                .andExpect(jsonPath("$[0].id").value(report.getId()))
                .andExpect(jsonPath("$[0].wellBeing").value(report.getWellBeing()))
                .andExpect(jsonPath("$[0].date").value(report.getDate().toString()))
                .andExpect(jsonPath("$[0].changeBehavior").value(report.getChangeBehavior()))
                .andExpect(jsonPath("$[0].user.id").value(report.getUser().getId()))
                .andExpect(jsonPath("$[0].user.telegramId").value(report.getUser().getTelegramId()))
                .andExpect(jsonPath("$[0].animal.id").value(report.getAnimal().getId()))
                .andExpect(jsonPath("$[0].animal.name").value(report.getAnimal().getName()));
    }
}
