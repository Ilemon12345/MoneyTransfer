package com.yurkov.dao;

import com.yurkov.model.Account;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author yevhenii yurkov
 * @since 0.0.1
 */
@RegisterBeanMapper(Account.class)
public interface AccountDao {

    @SqlQuery("select * from ACCOUNT as a where a.id = :id")
    Account get(@Bind Long id);

    @SqlUpdate("insert into ACCOUNT (name, balance, version) " +
            "values(:name, :balance, :version)")
    void create(@BindBean Account account);

    @SqlUpdate("update ACCOUNT set " +
            "balance = :balance, " +
            "version = :version + 1 " +
            "where id = :id")
    void updateBalance(@BindBean Account account);

    @SqlQuery("select * from ACCOUNT as a where a.id = :id for update")
    Account getWithLock(@Bind Long id);

    @Transactional
    default void transfer(Account accountFrom, Account accountTo, BigDecimal balance) {
        Account lockedAccountTo;
        Account lockedAccountFrom;

        //To avoid deadlock we should get accounts with lock in strict order,
        // so id is auto-increment and we can rely on it in this case
        if (accountFrom.getId() > accountTo.getId()) {
            lockedAccountTo = getWithLock(accountTo.getId());
            lockedAccountFrom = getWithLock(accountFrom.getId());
        } else {
            lockedAccountFrom = getWithLock(accountFrom.getId());
            lockedAccountTo = getWithLock(accountTo.getId());
        }

        requireNonNull(lockedAccountFrom, "Account from doesn't exist, account : " + accountFrom);
        requireNonNull(lockedAccountTo, "Account to doesn't exist, account : " + accountTo);
        checkAccountVersions(accountFrom, lockedAccountFrom);
        checkAccountVersions(lockedAccountTo, accountTo);

        lockedAccountFrom.setBalance(accountFrom.getBalance().subtract(balance));
        lockedAccountTo.setBalance(accountTo.getBalance().add(balance));

        updateBalance(lockedAccountFrom);
        updateBalance(lockedAccountTo);
    }

    default void checkAccountVersions(Account account, Account lockedAccount) {
        if (!Objects.equals(account.getVersion(), lockedAccount.getVersion())) {
            throw new IllegalStateException("Accounts versions differ, it means that someone has already transferred money," +
                    "input account: " + account + ", " + "updated account " + lockedAccount);
        }
    }
}
