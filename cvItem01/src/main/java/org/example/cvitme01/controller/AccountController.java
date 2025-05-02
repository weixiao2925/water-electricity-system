package org.example.cvitme01.controller;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.Account;
import org.example.cvitme01.entity.vo.request.PasswordUpdateVO;
import org.example.cvitme01.service.AccountService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/info-detail")
    public RestBean<Account> getUserInfoDetail(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        Account accountWithDetails = accountService.findAccountWithDetailsById(id);
        return accountWithDetails == null
                ? RestBean.failure(400, "用户或详情不存在")
                : RestBean.success(accountWithDetails);
    }

    @PostMapping("/info-update")
    public RestBean<Void> updateUserInfo(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestBody Account account) {
        return accountService.updateAccountInfo(id, account)
                ? RestBean.success()
                : RestBean.failure(400, "未知错误，请联系管理员");
    }

    @PostMapping("/password-update")
    public RestBean<Void> updatePassword(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestBody PasswordUpdateVO vo) {
        String message = accountService.updateAccountPassword(id, vo);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }
}
