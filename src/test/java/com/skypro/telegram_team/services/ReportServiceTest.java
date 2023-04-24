package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @InjectMocks
    private ReportService reportService;
    @Mock
    private ReportRepository reportRepository;
    private Report expectedReport;

    @BeforeEach
    public void setup() {
        expectedReport = new Report();
        expectedReport.setId(1L);
        expectedReport.setDate(LocalDateTime.now());
        Animal animal = new Animal();
        animal.setId(1L);
        User user = new User();
        user.setId(1L);
        expectedReport.setAnimal(animal);
        expectedReport.setUser(user);
    }

    @Test
    public void createReport() {
        when(reportRepository.save(any())).thenReturn(expectedReport);
        Report actualReport = reportService.create(expectedReport);
        assertEquals(expectedReport, actualReport);
        verify(reportRepository, times(1)).save(any());
    }

    @Test
    public void findById() {
        when(reportRepository.findById(any())).thenReturn(Optional.ofNullable(expectedReport));
        Report actualReport = reportService.findById(expectedReport.getId());
        assertEquals(expectedReport, actualReport);
        verify(reportRepository, times(1)).findById(any());
    }

    @Test
    public void deleteById() {
        when(reportRepository.findById(any())).thenReturn(Optional.ofNullable(expectedReport));
        Report actualReport = reportService.deleteById(expectedReport.getId());
        assertEquals(expectedReport, actualReport);
        verify(reportRepository, times(1)).findById(any());
    }

    @Test
    public void findAll() {
        when(reportRepository.findAll()).thenReturn(List.of(expectedReport));
        List<Report> actualReports = reportService.findAll();
        assertTrue(actualReports.size() != 0);
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    public void updateReport() {
        Report reportInDB = new Report();
        reportInDB.setId(1L);
        reportInDB.setDiet("diet1");
        Report updatedReport = new Report();
        updatedReport.setId(2L);
        updatedReport.setDiet("diet2");
        when(reportRepository.findById(any())).thenReturn(Optional.of(reportInDB));
        when(reportRepository.save(any())).thenReturn(updatedReport);
        Report actualReport = reportService.update(updatedReport, reportInDB.getId());
        assertEquals(actualReport.getId(), reportInDB.getId());
        assertEquals(actualReport.getDiet(), updatedReport.getDiet());
        verify(reportRepository, times(1)).save(any());
        verify(reportRepository, times(1)).findById(any());
    }

    @Test
    public void findByAnimalId() {
        List<Report> expectedReports = List.of(expectedReport);
        when(reportRepository.findByAnimalId(anyLong())).thenReturn(expectedReports);
        List<Report> actualReports = reportService.findByAnimalId(expectedReport.getAnimal().getId());
        assertEquals(expectedReports, actualReports);
        verify(reportRepository, times(1)).findByAnimalId(anyLong());
    }

    @Test
    public void findByUserId() {
        List<Report> expectedReports = List.of(expectedReport);
        when(reportRepository.findByUserId(any())).thenReturn(expectedReports);
        Collection<Report> actualReports = reportService.findByUserId(expectedReport.getUser().getId());
        assertEquals(expectedReports, actualReports);
        verify(reportRepository, times(1)).findByUserId(any());
    }

    @Test
    public void findByUserIdAndDate() {
        List<Report> expectedReports = List.of(expectedReport);
        when(reportRepository.findByUserIdAndDate(any(), any())).thenReturn(expectedReports);
        Collection<Report> actualReports = reportService.findByUserIdAndDate(expectedReport.getUser().getId(),
                expectedReport.getDate());
        assertEquals(expectedReports, actualReports);
        verify(reportRepository, times(1)).findByUserIdAndDate(any(), any());
    }

    @Test
    public void findFirstByUserIdAndDate() {
        List<Report> expectedReports = List.of(expectedReport);
        when(reportRepository.findAll()).thenReturn(expectedReports);
        Report actualReport = reportService.findFirstByUserIdAndDate(expectedReport.getUser().getId(),
                expectedReport.getDate());
        assertEquals(expectedReports.get(0), actualReport);
        verify(reportRepository, times(1)).findAll();
    }
}
