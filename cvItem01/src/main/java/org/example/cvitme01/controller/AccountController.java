package org.example.cvitme01.controller;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.Account;
import org.example.cvitme01.service.AccountService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/info")
    public RestBean<Account> getUserInfo(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account vo = accountService.findAccountInfoById(id);
        return vo == null
                ? RestBean.failure(400, "用户不存在")
                : RestBean.success(vo);
    }
}
