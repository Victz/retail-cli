package com.example.retail;

import com.example.retail.domain.Debt;
import com.example.retail.domain.User;
import com.example.retail.repository.DebtRepository;
import com.example.retail.security.SecurityUtils;
import com.example.retail.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author shawn
 */
@ShellComponent
public class Command {

    @Autowired
    UserService userService;

    @Autowired
    DebtRepository debtRepository;

    @ShellMethod("Login <User>")
    public String login(String username) {
        StringBuilder output = new StringBuilder();

        User user = userService.findOneByUsername(username);

        // Create user if user doesn't exist
        if (user == null) {
            user = userService.create(username);
            output.append("New User \"").append(username).append("\" created. \n");
        }

        output.append("Hello, ").append(username).append("!\n");

        SecurityUtils.setUser(user);  // Save user session to static varible
        output.append(showBalanceAndDebt(SecurityUtils.getUser()));
        return output.toString();
    }

    @Transactional
    @ShellMethod("Topup <Amount>.")
    public String topup(BigDecimal amount) {

        StringBuilder output = new StringBuilder();

        // Check if user logged in.
        if (SecurityUtils.getUser() == null) {
            output.append("Please login first!");
            return output.toString();
        }

        userService.deposit(SecurityUtils.getUser(), amount);
        userService.updateBalance(SecurityUtils.getUser(), SecurityUtils.getUser().getBalance().add(amount));

        // Clear if any pending debt
        List<Debt> debts = debtRepository.findByDebitor(SecurityUtils.getUser());
        if (debts != null && !debts.isEmpty()) {
            for (Debt debt : debts) {
                if (SecurityUtils.getUser().getBalance().compareTo(debt.getAmount()) > 0) {

                    userService.withdraw(SecurityUtils.getUser(), debt.getAmount());
                    userService.updateBalance(SecurityUtils.getUser(), SecurityUtils.getUser().getBalance().subtract(debt.getAmount()));
                    userService.deposit(debt.getCreditor(), debt.getAmount());
                    userService.updateBalance(debt.getCreditor(), debt.getCreditor().getBalance().add(debt.getAmount()));
                    debtRepository.delete(debt);

                    output.append("Transferred ").append(debt.getAmount()).append(" to ").append(debt.getCreditor().getUsername()).append(".\n");

                } else if (SecurityUtils.getUser().getBalance().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal transactionAmount = SecurityUtils.getUser().getBalance();
                    userService.withdraw(SecurityUtils.getUser(), transactionAmount);
                    userService.updateBalance(SecurityUtils.getUser(), SecurityUtils.getUser().getBalance().subtract(transactionAmount));
                    userService.deposit(debt.getCreditor(), transactionAmount);
                    userService.updateBalance(debt.getCreditor(), debt.getCreditor().getBalance().add(transactionAmount));

                    BigDecimal newDebtAmount = debt.getAmount().subtract(transactionAmount);
                    debt.setAmount(newDebtAmount);
                    debtRepository.save(debt);

                    output.append("Transferred ").append(transactionAmount).append(" to ").append(debt.getCreditor().getUsername()).append(".\n");
                } else {
                    break;
                }
            }

        }

        output.append(showBalanceAndDebt(SecurityUtils.getUser()));

        return output.toString();
    }

    @Transactional
    @ShellMethod("Pay <User> <Amount>")
    public String pay(String username, BigDecimal amount) {

        StringBuilder output = new StringBuilder();

        // Check if user logged in.
        if (SecurityUtils.getUser() == null) {
            output.append("Please login first!");
            return output.toString();
        }

        // Check if the payee exist.
        User payee = userService.findOneByUsername(username);
        if (payee == null) {
            output.append("Payee doesn't exist!");
            return output.toString();
        }

        // Clear if any pending debt
        List<Debt> debts = debtRepository.findByDebitorAndCreditor(payee, SecurityUtils.getUser());
        if (debts != null && !debts.isEmpty()) {
            for (Debt debt : debts) {
                if (amount.compareTo(debt.getAmount()) > 0) {

                    userService.withdraw(debt.getCreditor(), debt.getAmount());
                    userService.deposit(debt.getDebitor(), debt.getAmount());
                    debtRepository.delete(debt);
                    output.append("Transferred ").append(debt.getAmount()).append(" to ").append(debt.getDebitor().getUsername()).append(".\n");
                    amount = amount.subtract(debt.getAmount());

                } else if (amount.compareTo(BigDecimal.ZERO) > 0) {

                    userService.withdraw(debt.getCreditor(), amount);
                    userService.deposit(debt.getDebitor(), amount);
                    BigDecimal newDebtAmount = debt.getAmount().subtract(amount);
                    debt.setAmount(newDebtAmount);
                    debtRepository.save(debt);

                    output.append("Transferred ").append(amount).append(" to ").append(debt.getDebitor().getUsername()).append(".\n");
                    amount = BigDecimal.ZERO;

                } else {
                    break;
                }

            }
        }

        // Transfer the remaining amount
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            if (SecurityUtils.getUser().getBalance().compareTo(amount) > 0) {

                userService.withdraw(SecurityUtils.getUser(), amount);
                userService.updateBalance(SecurityUtils.getUser(), SecurityUtils.getUser().getBalance().subtract(amount));

                userService.deposit(payee, amount);
                userService.updateBalance(payee, payee.getBalance().add(amount));

                output.append("Transferred ").append(amount).append(" to ").append(username).append(".\n");

            } else {

                BigDecimal transactionAmount = SecurityUtils.getUser().getBalance();
                userService.withdraw(SecurityUtils.getUser(), transactionAmount);
                userService.updateBalance(SecurityUtils.getUser(), BigDecimal.ZERO);
                userService.deposit(payee, transactionAmount);
                userService.updateBalance(payee, payee.getBalance().add(transactionAmount));

                BigDecimal debtAmount = amount.subtract(transactionAmount);
                userService.createDebt(payee, SecurityUtils.getUser(), debtAmount);
                output.append("Transferred ").append(transactionAmount).append(" to ").append(username).append(".\n");
            }
        }
        output.append(showBalanceAndDebt(SecurityUtils.getUser()));
        return output.toString();
    }

    @ShellMethod("Display current user")
    public String whoami() {

        StringBuilder output = new StringBuilder();

        if (SecurityUtils.getUser() == null) {
            output.append("You are not login yet!");
        } else {
            output.append("Hello, ").append(SecurityUtils.getUser().getUsername()).append("!\n");
            output.append(showBalanceAndDebt(SecurityUtils.getUser()));
        }

        return output.toString();
    }

    @ShellMethod("Logout")
    public String logout() {

        StringBuilder output = new StringBuilder();

        if (SecurityUtils.getUser() == null) {
            output.append("You are not login yet!");
        } else {
            SecurityUtils.setUser(null);
            output.append("Log out successfully!");
        }
        return output.toString();
    }

    private String showBalanceAndDebt(User user) {

        StringBuilder output = new StringBuilder();

        output.append("Your balance is ").append(user.getBalance()).append(".\n");

        List<Debt> debts = debtRepository.findByDebitor(user);
        if (debts != null && !debts.isEmpty()) {
            debts.forEach(debt -> {
                output.append("Owning ").append(debt.getAmount()).append(" to ").append(debt.getCreditor().getUsername()).append(".\n");
            });
        }

        List<Debt> credit = debtRepository.findByCreditor(user);
        if (credit != null && !credit.isEmpty()) {
            credit.forEach(debt -> {
                output.append("Owning ").append(debt.getAmount()).append(" from ").append(debt.getDebitor().getUsername()).append(".\n");
            });
        }

        return output.toString();
    }

}
