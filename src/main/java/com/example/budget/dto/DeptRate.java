package com.example.budget.dto;

// 부서별 집행률 표의 한 줄
public class DeptRate {
    private String deptName;
    private Long budgetAmount;    // 편성액 합계
    private Long executedAmount;  // 집행액 (승인된 금액만)
    private Double rate;          // 집행률(%) = 집행액 / 편성액 x 100

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(Long budgetAmount) { this.budgetAmount = budgetAmount; }
    public Long getExecutedAmount() { return executedAmount; }
    public void setExecutedAmount(Long executedAmount) { this.executedAmount = executedAmount; }
    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
}
