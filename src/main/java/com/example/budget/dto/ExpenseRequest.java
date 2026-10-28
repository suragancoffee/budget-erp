package com.example.budget.dto;

import java.time.LocalDate;

// 집행 신청 한 건 = 이 객체 하나 (화면 입력값 + INSERT 후 생성된 번호)
public class ExpenseRequest {
    private Long expenseId;    // INSERT 후 DB가 만들어 준 번호가 여기에 채워짐
    private Long budgetId;     // 어느 예산(부서 x 과목)에서 쓸지
    private Long empId;        // 신청자
    private String title;      // 신청 내용
    private Long amount;       // 신청 금액
    private LocalDate reqDate; // 신청일 (화면에서 받지 않고 서버가 정함)

    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public Long getBudgetId() { return budgetId; }
    public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }
    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public LocalDate getReqDate() { return reqDate; }
    public void setReqDate(LocalDate reqDate) { this.reqDate = reqDate; }
}
