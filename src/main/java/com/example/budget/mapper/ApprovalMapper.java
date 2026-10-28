package com.example.budget.mapper;

import com.example.budget.dto.ApprovalHistory;
import com.example.budget.dto.PendingExpense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 메서드 이름 = ApprovalMapper.xml 안의 id
@Mapper
public interface ApprovalMapper {

    // 결재 대기 목록
    List<PendingExpense> selectPending();

    // 결재 이력 (최신순)
    List<ApprovalHistory> selectHistory();

    // 이 직원이 이 신청을 처리할 수 있는가? (역할이 결재자 + 신청 건의 부서와 같은 부서) 1이면 가능, 0이면 불가
    int countAllowedApprover(@Param("expenseId") Long expenseId, @Param("actorId") Long actorId);

    // 상태 변경. '대기'일 때만 바뀌고, 바뀐 행 수를 돌려줌 (0이면 이미 처리됨)
    int updateStatusIfPending(@Param("expenseId") Long expenseId, @Param("status") String status);

    // 결재 이력 추가 (이력은 쌓기만 하고 수정·삭제하지 않음)
    int insertHistory(@Param("expenseId") Long expenseId,
                      @Param("actorId") Long actorId,
                      @Param("action") String action,
                      @Param("memo") String memo);
}
