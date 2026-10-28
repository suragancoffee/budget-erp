package com.example.budget.dto;

// 신청자 선택 목록용 (담당자만)
public class Employee {
    private Long empId;
    private String empName;
    private String deptName;

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}
