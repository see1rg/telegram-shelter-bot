package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.repositories.ReportRepository;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Сервис для работы с отчетами
 */
@Log4j
@Service
public class ReportService {
    ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * сохранение отчета в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param report отчет для сохранения в БД
     * @return сохраненный отчет
     */
    @Transactional
    public Report save(Report report) {
        log.info("Saving report: " + report);
        return reportRepository.save(report);
    }

    /**
     * получение отчета по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}}
     *
     * @param id идентификатор отчета
     * @return найденный отчет
     */
    public Report findById(Long id) {
        log.info("Finding report by id: " + id);
        return reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
    }

    /**
     * удаление отчета по id из БД используя метод репозитория {@link JpaRepository#delete(Object)}}
     *
     * @param id идентификатор отчета
     * @return удаленный отчет
     */
    @Transactional
    public Report deleteById(Long id) {
        log.info("Deleting report by id: " + id);
        Report report = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        reportRepository.delete(report);
        return report;
    }

    /**
     * Получение всех отчетов из БД используя метод репозитория {@link JpaRepository#findAll()}}
     *
     * @return список найденных отчетов
     */
    public List<Report> findAll() {
        log.info("Finding all reports");
        return reportRepository.findAll();
    }

    /**
     * Обновление отчета в БД используя метод репозитория {@link JpaRepository#save(Object)}}
     *
     * @param report отчет для обновления
     * @param id идентификатор отчета
     * @return обновленный отчет
     */
    @Transactional
    public Report update(Report report, Long id) {
        log.info("Updating report: " + report);
        ModelMapper modelMapper = new ModelMapper();
        Report reportToUpdate = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        report.setId(id);
        modelMapper.map(report, reportToUpdate);
        return reportRepository.save(reportToUpdate);
    }

}
