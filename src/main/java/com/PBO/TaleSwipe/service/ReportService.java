package com.PBO.TaleSwipe.service;

import java.util.List;

import com.PBO.TaleSwipe.dto.ReportRequest;
import com.PBO.TaleSwipe.dto.ReportResponse;

public interface ReportService {
    ReportResponse createReport(ReportRequest request, String username);
    List<ReportResponse> getAllReports();
    List<ReportResponse> getReportsByContent(String contentId);
    List<ReportResponse> getReportsByType(String jenisLaporan);
    void deleteReport(String reportId);
} 
