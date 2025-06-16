package com.PBO.TaleSwipe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PBO.TaleSwipe.dto.ReportRequest;
import com.PBO.TaleSwipe.dto.ReportResponse;
import com.PBO.TaleSwipe.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.createReport(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<ReportResponse>> getReportsByContent(
            @PathVariable String contentId) {
        return ResponseEntity.ok(reportService.getReportsByContent(contentId));
    }

    @GetMapping("/type/{jenisLaporan}")
    public ResponseEntity<List<ReportResponse>> getReportsByType(
            @PathVariable String jenisLaporan) {
        return ResponseEntity.ok(reportService.getReportsByType(jenisLaporan));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }
} 

