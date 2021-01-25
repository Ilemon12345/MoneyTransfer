# MoneyTransfer

Using tack stack: Spring boot, Java8, PostgreSQL, JDBI3

The main idea of solving the current problem is in this method: AccountDao#transfer
The current solution uses two technics: version control of row and explicit locking in PostgreSQL + read-committed Isolation lvl.
1. Version control is used in order to resolve an issue when two users are going to do some operations with the same accounts and we have to indicate that another user modified these accounts already (just throwing an exception). 
2. Explicit locking is used to make sure that nobody is modifying target accounts balance during a transaction except the current user.

Flow: 
1. Users are trying to transfer money.
2. The first user's thread gets target accounts with lock, check that versions are the same and update accounts.
3. The second user's thread gets target accounts with lock, waits until the first user is finished, reexecutes get query with lock checks that versions differ and gets an error.

Note about deadlock: 
There might be situation when two accounts can be get in two different order:

User1: 
lockedAccountTo = getWithLock(accountTo.getId());
lockedAccountFrom = getWithLock(accountFrom.getId());
User2:
lockedAccountFrom = getWithLock(accountFrom.getId());
lockedAccountTo = getWithLock(accountTo.getId());

This case may be cause of a deadlock (in case with Postgres, an exception is thrown)
To resolve this issue accounts have to be fetched in strict order (in our case sequntial id helps us):
if (accountFrom.getId() > accountTo.getId()) {
      lockedAccountTo = getWithLock(accountTo.getId());
      lockedAccountFrom = getWithLock(accountFrom.getId());
} else {
      lockedAccountFrom = getWithLock(accountFrom.getId());
      lockedAccountTo = getWithLock(accountTo.getId());
}

Gaps of current solution:
Relying on RDBMS features: locks, isolation lvl that's why:
  1. The current shouldn't be work with NOSQL solution like cassandra
  2. If accounts are updated frequently then errors are realted to checking versions can be a problem
  
P.S. I didn't describe why @Transactional is used etc because this's project is really simple with a minimum magic :)
