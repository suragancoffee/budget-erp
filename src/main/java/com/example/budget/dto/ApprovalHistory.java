package com.example.budget.dto;

// 결재 이력 표의 한 줄 (누가 언제 무엇을 했는지)
public class ApprovalHistory {
    private Long histId;
    private Long expenseId;
    private String title;
    private String action;
    private String actorName;
    private String actionAt;
    private String memo;

    public Long getHistId() { return histId; }
    public void setHistId(Long histId) { this.histId = histId; }
    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }
    public String getActionAt() { return actionAt; }
    public void setActionAt(String actionAt) { this.actionAt = actionAt; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
