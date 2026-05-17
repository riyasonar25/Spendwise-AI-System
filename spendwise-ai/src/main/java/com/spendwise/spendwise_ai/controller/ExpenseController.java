package com.spendwise.spendwise_ai.controller;

import com.spendwise.spendwise_ai.dto.CategorySummaryDTO;
import com.spendwise.spendwise_ai.dto.ExpenseRequestDTO;
import com.spendwise.spendwise_ai.dto.ExpenseResponseDTO;
import com.spendwise.spendwise_ai.model.Expense;
import com.spendwise.spendwise_ai.service.ExpenseService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ✅ ADD EXPENSE
    @PostMapping
    public ExpenseResponseDTO createExpense(@RequestBody ExpenseRequestDTO requestDTO) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.createExpense(requestDTO, email);
    }


    // ✅ GET EXPENSE BY ID
   @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getExpenseById(id, email);
    }


    // ✅ GET ALL EXPENSES
        @GetMapping
    public List<Expense> getAllExpenses() {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getAllExpenses(email);
    }

   
// ✅ GET EXPENSES BY DATE

@GetMapping("/date/{date}")
public List<Expense> getExpensesByDate(@PathVariable LocalDate date) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getExpensesByDate(date, email);
}


    // ✅ UPDATE EXPENSE
   @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id,
                             @RequestBody Expense expense) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.updateExpense(id, expense, email);
    }


    // ✅ DELETE EXPENSE
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    expenseService.deleteExpense(id, email);
    }


    // ✅ TOTAL EXPENSE BY DATE
    @GetMapping("/total-by-date")
    public Double getTotalExpenseByDate(@RequestParam LocalDate date) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getTotalExpenseByDate(date, email);
    }


    // ✅ TOTAL EXPENSE BETWEEN DATES
   @GetMapping("/total-between")
    public Double getTotalExpenseBetweenDates(@RequestParam LocalDate start,
                                          @RequestParam LocalDate end) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getTotalExpenseBetweenDates(start, end, email);
    }


    // ✅ CATEGORY SUMMARY BETWEEN DATES
    @GetMapping("/summary-between")
    public List<CategorySummaryDTO> getCategorySummaryBetweenDates(
        @RequestParam LocalDate start,
        @RequestParam LocalDate end) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getCategorySummaryBetweenDates(start, end, email);
    }

    // ✅ MONTHLY CATEGORY SUMMARY
    @GetMapping("/summary-month")
    public List<CategorySummaryDTO> getCategorySummaryByMonth(
        @RequestParam int year,
        @RequestParam int month) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.getCategorySummaryByMonth(year, month, email);
    }
    


}
