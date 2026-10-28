package com.example.budget.controller;

import com.example.budget.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // http://localhost:8080/reports  (연도 지정: /reports?year=2026)
    @GetMapping("/reports")
    public String deptRate(@RequestParam(name = "year", defaultValue = "2026") int year, Model model) {
        model.addAttribute("year", year);
        model.addAttribute("rates", reportService.getDeptRates(year));
        return "dept-rate";
    }
}
