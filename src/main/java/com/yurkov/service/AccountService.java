package com.yurkov.service;

import com.yurkov.dao.AccountDao;
import com.yurkov.model.Account;
import com.yurkov.model.Transfer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yevhenii yurkov
 * @since 0.0.1
 */
@Service
public class AccountService {

    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void transferAmount(Transfer transfer) {
        Account existingAccountFrom = get(transfer.getAccountFromId());
        Account existingAccountTo = get(transfer.getAccountToId());

        transferAmount(existingAccountFrom, existingAccountTo, transfer.getAmount());
    }

    public void transferAmount(Account from, Account to, BigDecimal amount) {
        validate(from, to, amount);

        //some external calls to other microservices can be done here

        accountDao.transfer(from, to, amount);
    }

    public void create(Account account) {
        account.setVersion(0);
        accountDao.create(account);
    }

    public Account get(Long id) {
        return accountDao.get(id);
    }

    private void validate(Account from, Account to, BigDecimal amount) {
        Objects.requireNonNull(from, "From account is null");
        Objects.requireNonNull(to, "To account is null");

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount can be less than 0, amount: " + amount);
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Balance from account must be more than amount, " +
                    "from account " + from +
                    "amount" + amount);
        }
    }
}
