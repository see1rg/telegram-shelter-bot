package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {
    final private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Поиск отчета по id", description = "Поиск отчета по id", tags = "Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет найден по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class))
            })
    })
    @GetMapping("/{id}")
    public Report getReport(@Parameter(description = "Id отчета") @PathVariable Long id) {
        return reportService.findById(id);
    }

    @Operation(summary = "Получение списка всех отчетов", description = "Получение списка всех отчетов", tags = "Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список всех отчетов", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Report.class)))
            })
    })
    @GetMapping
    public Iterable<Report> getAllReports() {
        return reportService.findAll();
    }

    @Operation(summary = "Удаление отчета по id", description = "Удаление отчета по id", tags = "Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет удален", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class))
            })
    })
    @DeleteMapping("/{id}")
    public Report deleteReport(@Parameter(description = "Id отчета") @PathVariable Long id) {
        return reportService.deleteById(id);
    }

    @Operation(summary = "Создание отчета", description = "Создание отчета", tags = "Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет создан", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class))
            })
    })
    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.create(report);
    }

    @Operation(summary = "Изменение отчета", description = "Изменение отчета", tags = "Reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Измененный отчет", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class))
            })
    })
    @PutMapping("/{id}")
    public Report updateReport(@RequestBody Report report, @Parameter(description = "Id отчета") @PathVariable Long id) {
        return reportService.update(report, id);
    }
}
