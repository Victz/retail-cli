package com.example.retail.repository;

import com.example.retail.domain.Debt;
import com.example.retail.domain.Transaction;
import com.example.retail.domain.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private DebtRepository debtRepository;

    /**
     * Test of create method.
     */
    @Test
    public void testCreate() {

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username1");
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedBy("test");
        user.setLastModifiedBy("test");
        userRepository.save(user);

        User user2 = userRepository.findOneByUsername("username1").get();
        assertEquals(user, user2);
    }

    /**
     * Test of deposit method.
     */
    @Test
    public void testDeposit() {

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username_deposit");
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedBy("test");
        user.setLastModifiedBy("test");
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setType(Transaction.Type.DEPOSIT);
        transaction.setUser(user);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setAmount(new BigDecimal("100"));
        transaction.setCreatedBy("test");
        transaction.setLastModifiedBy("test");
        transactionRepository.save(transaction);

        Transaction transaction2 = transactionRepository.findById(transaction.getId()).get();
        assertEquals(transaction, transaction2);
    }

    /**
     * Test of withdraw method.
     */
    @Test
    public void testWithdraw() {

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username_withdraw");
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedBy("test");
        user.setLastModifiedBy("test");
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setType(Transaction.Type.WITHDRAW);
        transaction.setUser(user);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setAmount(new BigDecimal("100"));
        transaction.setCreatedBy("test");
        transaction.setLastModifiedBy("test");
        transactionRepository.save(transaction);

        Transaction transaction2 = transactionRepository.findById(transaction.getId()).get();
        assertEquals(transaction, transaction2);
    }

    /**
     * Test of debt update method.
     */
    @Test
    public void testDebtUpdate() {

        User creditor = new User();
        creditor.setId(UUID.randomUUID().toString());
        creditor.setUsername("creditor");
        creditor.setBalance(BigDecimal.ZERO);
        creditor.setCreatedBy("test");
        creditor.setLastModifiedBy("test");
        userRepository.save(creditor);

        User debitor = new User();
        debitor.setId(UUID.randomUUID().toString());
        debitor.setUsername("debitor");
        debitor.setBalance(BigDecimal.ZERO);
        debitor.setCreatedBy("test");
        debitor.setLastModifiedBy("test");
        userRepository.save(debitor);

        Debt debt = new Debt();
        debt.setId(UUID.randomUUID().toString());
        debt.setCreditor(creditor);
        debt.setDebitor(debitor);
        debt.setAmount(new BigDecimal("100"));
        debtRepository.save(debt);

        Debt debt2 = debtRepository.findById(debt.getId()).get();
        assertEquals(debt, debt2);
    }

}
