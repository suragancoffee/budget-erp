package com.example.budget.exception;

// 업무 규칙 위반(잔액 부족, 금액 오류 등)을 알리는 예외.
// RuntimeException을 상속하므로 @Transactional 메서드에서 던지면 자동으로 롤백된다.
public class BudgetException extends RuntimeException {
    public BudgetException(String message) {
        super(message);
    }
}
