package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.repositories.ReportRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Log4j
@Service
public class ReportService {
    ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    @Transactional
    public Report save(Report report) {
        log.info("Saving report: " + report);
        return reportRepository.save(report);
    }

//    @Transactional
//    public Report update(Report report) {
//        log.info("Updating report: " + report);
//        return reportRepository.updateReportById(report.getId(), report);
//    }

    public Report findById(Long id) {
        log.info("Finding report by id: " + id);
        return reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
    }
    @Transactional
    public Report deleteById(Long id) {
        log.info("Deleting report by id: " + id);
        Report report = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        reportRepository.delete(report);
        return report;
    }

    public List<Report> findAll() {
        log.info("Finding all reports");
        return reportRepository.findAll();
    }

}
