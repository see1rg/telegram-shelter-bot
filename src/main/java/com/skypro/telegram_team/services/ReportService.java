package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.repositories.ReportRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

/**
 * Сервис для работы с отчетами
 */
@Log4j2
@Service
public class ReportService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    /**
     * сохранение отчета в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param report отчет для сохранения в БД
     * @return сохраненный отчет
     */
    @Transactional
    public Report create(Report report) {
        log.info("Saving report: " + report);
//        validate(report);
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
     * @param id     идентификатор отчета
     * @return обновленный отчет
     */
    @Transactional
    public Report update(Report report, Long id) {
        log.info("Updating report: " + report);
//        validate(report);
        ModelMapper modelMapper = new ModelMapper();
        Report reportToUpdate = reportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report not found"));
        report.setId(id);
        modelMapper.map(report, reportToUpdate);
        return reportRepository.save(reportToUpdate);
    }

    /**
     * Получение отчетов по идентификатору из БД используя метод репозитория {@link ReportRepository#findByAnimalId(long)}}
     *
     * @param id
     * @return
     */
    public List<Report> findByAnimalId(long id) {
        log.info("Finding reports by animal id: " + id);
        return reportRepository.findByAnimalId(id);
    }

    /**
     * Поиск отчетов по пользователю используя метод репозитория {@link ReportRepository#findByUserId(Long)}}
     *
     * @param userId
     * @return
     */
    public Collection<Report> findByUserId(Long userId) {
        return reportRepository.findByUserId(userId);
    }

    /**
     * Поиск отчетов пользователя по id и дате используя метод репозитория
     * {@link ReportRepository#findByUserIdAndDate(Long, LocalDateTime)}}
     *
     * @param userId
     * @param dateTime
     * @return
     */
    public Collection<Report> findByUserIdAndDate(Long userId, LocalDateTime dateTime) {
        return reportRepository.findByUserIdAndDate(userId, dateTime);
    }

    /**
     * Поиск отчета пользователя по дате
     * Если отчет не найден, то возвращаем пустой отчет
     *
     * @param userId
     * @param dateTime
     * @return
     */
    public Report findFirstByUserIdAndDate(Long userId, LocalDateTime dateTime) {
        //почему-то не выбирает данные из БД по дате, поэтому пока фильтруем здесь
        LocalDateTime finalDateTime = dateTime.truncatedTo(ChronoUnit.DAYS);
        return findAll().stream()
                .filter(r -> r.getUser().getId() == userId &&
                        r.getDate().toLocalDate().isEqual(finalDateTime.toLocalDate()))
                .findFirst()
                .orElse(new Report());
    }

    /**
     * Проверить данные отчета, если данные некорректны то выбросить исключение {@link InvalidDataException}
     *
     * @param report
     */
//    private void validate(Report report) {
//        if (report.getUser() == null) {
//            throw new InvalidDataException("Отчет без пользователя");
//        }
//        if (report.getAnimal() == null) {
//            throw new InvalidDataException("Отчет без животного");
//        }
//        if (!report.getUser().getState().equals(User.OwnerStateEnum.PROBATION)) {
//            throw new InvalidDataException("Пользователь должен быть на испытательном сроке");
//        }
//    }
}
