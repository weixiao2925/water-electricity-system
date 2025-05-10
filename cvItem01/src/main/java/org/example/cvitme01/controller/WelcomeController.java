package org.example.cvitme01.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.vo.request.EmailRegisterVO;
import org.example.cvitme01.entity.vo.request.EmailResetVO;
import org.example.cvitme01.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class WelcomeController {

    private final AccountService accountService;


    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email){
        String message = accountService.registerEmailVerifyCode(email);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody EmailRegisterVO vo){
        String message=accountService.registerEmailAccount(vo);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
        String message = accountService.resetEmailAccountPassword(vo);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }
}
