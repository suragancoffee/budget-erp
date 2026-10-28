package com.example.budget.mapper;

import com.example.budget.dto.ExpenseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 메서드 이름 = ExpenseMapper.xml 안의 id
@Mapper
public interface ExpenseMapper {

    // 잔액 조회 + 그 예산 행을 잠금(FOR UPDATE). 없는 예산이면 null
    Long selectRemainForUpdate(@Param("budgetId") Long budgetId);

    // 집행 신청 INSERT. 생성된 expense_id가 req.expenseId에 채워짐
    int insertExpense(ExpenseRequest req);

    // 결재 이력 INSERT (이력은 쌓기만 하고 수정·삭제하지 않음)
    int insertHistory(@Param("expenseId") Long expenseId,
                      @Param("actorId") Long actorId,
                      @Param("action") String action);
}
