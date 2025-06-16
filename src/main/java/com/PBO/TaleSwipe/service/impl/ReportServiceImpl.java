package com.PBO.TaleSwipe.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO.TaleSwipe.dto.ReportRequest;
import com.PBO.TaleSwipe.dto.ReportResponse;
import com.PBO.TaleSwipe.model.Report;
import com.PBO.TaleSwipe.model.Story;
import com.PBO.TaleSwipe.model.User;
import com.PBO.TaleSwipe.repository.ReportRepository;
import com.PBO.TaleSwipe.repository.StoryRepository;
import com.PBO.TaleSwipe.repository.UserRepository;
import com.PBO.TaleSwipe.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @Override
    @Transactional
    public ReportResponse createReport(ReportRequest request, String username) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Story content = storyRepository.findById(request.getContentId())
                .orElseThrow(() -> new RuntimeException("Content not found"));

        Report report = Report.builder()
                .jenisLaporan(request.getJenisLaporan())
                .reportText(request.getReportText())
                .sender(sender)
                .content(content)
                .build();

        Report savedReport = reportRepository.save(report);
        return mapToResponse(savedReport);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getReportsByContent(String contentId) {
        Story content = storyRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return reportRepository.findByContent(content).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getReportsByType(String jenisLaporan) {
        return reportRepository.findByJenisLaporan(jenisLaporan).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
    }

    private ReportResponse mapToResponse(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .jenisLaporan(report.getJenisLaporan())
                .reportText(report.getReportText())
                .senderUsername(report.getSender().getUsername())
                .contentId(report.getContent().getStoryId())
                .build();
    }
} 
