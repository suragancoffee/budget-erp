package com.example.budget.service;

import com.example.budget.dto.BudgetStatus;
import com.example.budget.mapper.BudgetMapper;
import org.springframework.stereotype.Service;

import java.util.List;

// 업무 규칙이 들어가는 곳. 조회는 단순해서 Mapper를 그대로 부르지만,
// 다음 단계(집행 신청)에서 잔액 검증과 @Transactional이 여기 들어감
@Service
public class BudgetService {

    private final BudgetMapper budgetMapper;

    public BudgetService(BudgetMapper budgetMapper) {
        this.budgetMapper = budgetMapper;
    }

    public List<BudgetStatus> getBudgetStatus(int year) {
        return budgetMapper.selectBudgetStatus(year);
    }
}
