package com.spendwise.spendwise_ai.service.serviceimpl;

import com.spendwise.spendwise_ai.model.Income;
import com.spendwise.spendwise_ai.model.User;
import com.spendwise.spendwise_ai.repository.IncomeRepository;
import com.spendwise.spendwise_ai.repository.UserRepository;
import com.spendwise.spendwise_ai.service.IncomeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeServiceImpl(IncomeRepository incomeRepository,
                             UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    // ✅ ADD INCOME (User auto-set from JWT email)
   @Override
    public Income addIncome(Income income, String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    income.setUser(user);

    // Auto set month from date
    income.setMonth(income.getDate().getMonthValue());

    return incomeRepository.save(income);
    }

    // ✅ GET ALL USER INCOME
    @Override
    public List<Income> getAllIncome(String email) {
        return incomeRepository.findByUserEmail(email);
    }

    // ✅ TOTAL INCOME BY MONTH (USER SAFE)
    @Override
    public double getTotalIncomeByMonth(int year, int month, String email) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return incomeRepository
                .findByUserEmailAndDateBetween(email, startDate, endDate)
                .stream()
                .mapToDouble(Income::getAmount)
                .sum();
    }

    // ✅ DELETE (Optional: make user-safe later)
    @Override
    public void deleteIncome(Long id) {
        incomeRepository.deleteById(id);
    }
    
}