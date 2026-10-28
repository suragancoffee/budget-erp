package com.example.budget.mapper;

import com.example.budget.dto.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    // 신청자로 고를 수 있는 사람 = 역할이 '담당자'인 직원
    List<Employee> selectRequesters();

    // 결재 처리자로 고를 수 있는 사람 = 역할이 '결재자'인 직원
    List<Employee> selectApprovers();
}
