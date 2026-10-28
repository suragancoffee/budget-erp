package com.example.budget.controller;

import com.example.budget.exception.BudgetException;
import com.example.budget.mapper.EmployeeMapper;
import com.example.budget.service.ApprovalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final EmployeeMapper employeeMapper;

    public ApprovalController(ApprovalService approvalService, EmployeeMapper employeeMapper) {
        this.approvalService = approvalService;
        this.employeeMapper = employeeMapper;
    }

    // 결재함: http://localhost:8080/approvals
    @GetMapping
    public String list(Model model) {
        model.addAttribute("pending", approvalService.getPending());
        model.addAttribute("history", approvalService.getHistory());
        model.addAttribute("approvers", employeeMapper.selectApprovers());
        return "approval-list";
    }

    // 승인/반려 버튼. 처리 후에는 redirect로 목록을 다시 불러옴 (새로고침해도 중복 처리되지 않게)
    @PostMapping("/{expenseId}")
    public String process(@PathVariable("expenseId") Long expenseId,
                          @RequestParam(name = "actorId", required = false) Long actorId,
                          @RequestParam("action") String action,
                          @RequestParam(name = "memo", required = false) String memo,
                          RedirectAttributes redirect) {
        try {
            approvalService.process(expenseId, actorId, action, memo);
            redirect.addFlashAttribute("message", expenseId + "번 신청을 " + action + " 처리했습니다.");
        } catch (BudgetException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/approvals";
    }
}
