package com.example.budget.dto;

// 예산 현황 화면의 표 한 줄 = 이 객체 하나
public class BudgetStatus {
    private Long budgetId;
    private String deptName;
    private String itemName;
    private Long amount;      // 편성액
    private Long usedAmount;  // 사용 예정액 (승인 + 대기)
    private Long remain;      // 잔액

    public Long getBudgetId() { return budgetId; }
    public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public Long getUsedAmount() { return usedAmount; }
    public void setUsedAmount(Long usedAmount) { this.usedAmount = usedAmount; }
    public Long getRemain() { return remain; }
    public void setRemain(Long remain) { this.remain = remain; }
}
