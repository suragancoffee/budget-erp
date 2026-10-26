package com.example.budget.controller;

import com.example.budget.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 브라우저 요청을 받아서 Service를 부르고, 결과를 화면(HTML)에 넘김
@Controller
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // http://localhost:8080/budgets  (연도 지정: /budgets?year=2026)
    @GetMapping("/budgets")
    public String list(@RequestParam(defaultValue = "2026") int year, Model model) {
        model.addAttribute("year", year);
        model.addAttribute("budgets", budgetService.getBudgetStatus(year));
        return "budget-list";   // templates/budget-list.html
    }
}
