package com.example.budget.service;

import com.example.budget.dto.ExpenseRequest;
import com.example.budget.exception.BudgetException;
import com.example.budget.mapper.ExpenseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ExpenseService {

    private final ExpenseMapper expenseMapper;

    public ExpenseService(ExpenseMapper expenseMapper) {
        this.expenseMapper = expenseMapper;
    }

    // @Transactional: 이 메서드 안의 SQL 3개(잠금 조회, 신청 INSERT, 이력 INSERT)가
    // 전부 성공하면 COMMIT, 중간에 예외가 나면 전부 ROLLBACK (신청만 들어가고 이력이 빠지는 일이 없음)
    @Transactional
    public Long requestExpense(ExpenseRequest req) {

        // 1) 입력값 검증 - 잠금 잡기 전에 끝내서 잠금 시간을 짧게
        if (req.getBudgetId() == null || req.getEmpId() == null) {
            throw new BudgetException("예산 과목과 신청자를 선택하세요.");
        }
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new BudgetException("신청 내용을 입력하세요.");
        }
        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new BudgetException("금액은 1원 이상이어야 합니다.");
        }

        // 2) 잔액 조회 + 잠금 - 같은 예산에 동시에 신청이 들어와도 한 번에 한 건씩만 통과
        Long remain = expenseMapper.selectRemainForUpdate(req.getBudgetId());
        if (remain == null) {
            throw new BudgetException("존재하지 않는 예산입니다.");
        }

        // 3) 잔액 검증 - 화면에서 막아도 우회할 수 있으므로 서버에서 반드시 다시 검사
        if (req.getAmount() > remain) {
            throw new BudgetException(String.format(
                    "잔액이 부족합니다. (잔액 %,d원 / 신청 %,d원)", remain, req.getAmount()));
        }

        // 4) 신청 INSERT (상태는 DB 기본값 '대기') - 신청일은 서버가 정함
        req.setReqDate(LocalDate.now());
        expenseMapper.insertExpense(req);

        // 5) 결재 이력에 '신청' 한 줄 추가
        expenseMapper.insertHistory(req.getExpenseId(), req.getEmpId(), "신청");

        return req.getExpenseId();
    }
}
