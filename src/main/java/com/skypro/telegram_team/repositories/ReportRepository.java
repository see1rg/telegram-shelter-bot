package com.skypro.telegram_team.repositories;

import com.skypro.telegram_team.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
//    Report updateReportById(Long id, Report report);
}
