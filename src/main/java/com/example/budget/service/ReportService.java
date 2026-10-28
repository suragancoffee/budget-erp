package com.example.budget.service;

import com.example.budget.dto.DeptRate;
import com.example.budget.mapper.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.List;

// 집계 조회만 하므로 업무 규칙은 없음 (계산은 SQL이 함)
@Service
public class ReportService {

    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public List<DeptRate> getDeptRates(int year) {
        return reportMapper.selectDeptRate(year);
    }
}
