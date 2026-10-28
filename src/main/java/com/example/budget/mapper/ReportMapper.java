package com.example.budget.mapper;

import com.example.budget.dto.DeptRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {
    // 부서별 편성액 / 집행액 / 집행률
    List<DeptRate> selectDeptRate(@Param("year") int year);
}
