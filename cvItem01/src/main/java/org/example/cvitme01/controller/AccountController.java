package org.example.cvitme01.controller;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.dto.Account;
import org.example.cvitme01.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/test")
    public Account test() {
        return accountService.findByUsername("test");
    }
}
