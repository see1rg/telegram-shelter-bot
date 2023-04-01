package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.services.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {
    final private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{id}")
    public Report getReport(@PathVariable Long id) {
        return reportService.findById(id);
    }

    @GetMapping
    public Iterable<Report> getAllReports() {
        return reportService.findAll();
    }

    @DeleteMapping("/del/{id}")
    public Report deleteReport(@PathVariable Long id) {
        return reportService.deleteById(id);
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.save(report);
    }

//    @PutMapping("/{id}")
//    public Report updateReport(@RequestBody Report report, @PathVariable Long id) {
//        return reportService.update(report);
//    }
}
