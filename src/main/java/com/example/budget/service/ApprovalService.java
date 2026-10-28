package com.example.budget.service;

import com.example.budget.dto.ApprovalHistory;
import com.example.budget.dto.PendingExpense;
import com.example.budget.exception.BudgetException;
import com.example.budget.mapper.ApprovalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalMapper approvalMapper;

    public ApprovalService(ApprovalMapper approvalMapper) {
        this.approvalMapper = approvalMapper;
    }

    public List<PendingExpense> getPending() {
        return approvalMapper.selectPending();
    }

    public List<ApprovalHistory> getHistory() {
        return approvalMapper.selectHistory();
    }

    // 승인/반려 처리. 상태 UPDATE + 이력 INSERT를 한 트랜잭션으로 묶음
    @Transactional
    public void process(Long expenseId, Long actorId, String action, String memo) {

        // 1) 입력값 검증
        if (!"승인".equals(action) && !"반려".equals(action)) {
            throw new BudgetException("처리 구분이 올바르지 않습니다.");
        }
        if (actorId == null) {
            throw new BudgetException("결재자를 선택하세요.");
        }
        String cleanMemo = (memo == null || memo.isBlank()) ? null : memo.trim();
        if ("반려".equals(action) && cleanMemo == null) {
            throw new BudgetException("반려 사유를 입력하세요.");   // 반려는 사유가 남아야 함
        }

        // 2) 권한 검증 - 결재자 역할이면서 그 신청 건의 부서 소속이어야 함 (화면에서 막아도 서버에서 다시 검사)
        if (approvalMapper.countAllowedApprover(expenseId, actorId) == 0) {
            throw new BudgetException("해당 부서의 결재자만 처리할 수 있습니다.");
        }

        // 3) 상태 변경 - WHERE status = '대기' 조건 덕분에 이미 처리된 건은 바뀌지 않고 0건이 돌아옴
        //    (두 사람이 동시에 같은 건을 처리해도 먼저 처리한 한 명만 성공)
        int updated = approvalMapper.updateStatusIfPending(expenseId, action);
        if (updated == 0) {
            throw new BudgetException("이미 처리되었거나 존재하지 않는 신청입니다.");
        }

        // 4) 결재 이력 추가 - 상태 변경과 같은 트랜잭션이라 둘 다 저장되거나 둘 다 취소됨
        approvalMapper.insertHistory(expenseId, actorId, action, cleanMemo);
    }
}
