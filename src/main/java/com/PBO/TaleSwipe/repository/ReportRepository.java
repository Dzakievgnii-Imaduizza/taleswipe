package com.PBO.TaleSwipe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PBO.TaleSwipe.model.Report;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findBySender(User sender);
    List<Report> findByContent(Story content);
    List<Report> findByJenisLaporan(String jenisLaporan);
} 
