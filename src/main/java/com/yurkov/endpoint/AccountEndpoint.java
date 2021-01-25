package com.yurkov.endpoint;

import com.yurkov.model.Account;
import com.yurkov.model.Transfer;
import com.yurkov.service.AccountService;
import org.springframework.web.bind.annotation.*;

/**
 * @author yevhenii yurkov
 * @since 0.0.1
 */
@RestController
@RequestMapping("/account")
public class AccountEndpoint {

    private final AccountService accountService;

    public AccountEndpoint(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("")
    public void create(@RequestBody Account account) {
        accountService.create(account);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody Transfer transfer) {
        accountService.transferAmount(transfer);
    }

    @GetMapping("/{id}")
    public Account get(@PathVariable("id") Long id) {
        return accountService.get(id);
    }
}
