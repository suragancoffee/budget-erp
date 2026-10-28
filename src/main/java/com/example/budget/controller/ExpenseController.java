package com.example.budget.controller;

import com.example.budget.dto.ExpenseRequest;
import com.example.budget.exception.BudgetException;
import com.example.budget.mapper.EmployeeMapper;
import com.example.budget.service.BudgetService;
import com.example.budget.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private static final int YEAR = 2026;

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final EmployeeMapper employeeMapper;

    public ExpenseController(ExpenseService expenseService,
                             BudgetService budgetService,
                             EmployeeMapper employeeMapper) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.employeeMapper = employeeMapper;
    }

    // 신청 화면: http://localhost:8080/expenses/new
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("form", new ExpenseRequest());
        fillOptions(model);
        return "expense-form";
    }

    // 신청 버튼: 성공하면 예산 현황으로, 업무 규칙 위반이면 입력값을 유지한 채 신청 화면에 오류 표시
    @PostMapping
    public String submit(@ModelAttribute("form") ExpenseRequest form,
                         Model model, RedirectAttributes redirect) {
        try {
            expenseService.requestExpense(form);
            redirect.addFlashAttribute("message", "집행 신청이 접수되었습니다. (결재 대기)");
            return "redirect:/budgets";
        } catch (BudgetException e) {
            model.addAttribute("error", e.getMessage());
            fillOptions(model);
            return "expense-form";
        }
    }

    // 선택 목록(예산 과목, 신청자)을 화면에 넘김
    private void fillOptions(Model model) {
        model.addAttribute("budgets", budgetService.getBudgetStatus(YEAR));
        model.addAttribute("employees", employeeMapper.selectRequesters());
    }
}
