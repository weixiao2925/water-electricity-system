package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.repository.AccountRepository;
import org.example.cvitme01.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final JSqlClient sqlClient;

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account findAccountInfoById(Integer id) {
        AccountTable table = AccountTable.$;
        Fetcher<Account> fetcher = AccountFetcher.$
                .username()
                .email()
                .role()
                .avatar()
                .registerTime();
        return sqlClient
                .createQuery(table)
                .where(table.id().eq(Long.valueOf(id)))
                .select(table.fetch(fetcher))
                .fetchOneOrNull();
    }

    @Override
    public Account findAccountWithDetailsById(Integer id) {
        AccountTable table = AccountTable.$;
        // 定义 Fetcher，包含 Account 的所有标量字段和关联的 AccountDetails 的所有标量字段
        Fetcher<Account> fetcher = AccountFetcher.$
                .username()
                .email()
                .avatar()
                .registerTime()
                .details( // 获取关联的 details (AccountDetails)
                        AccountDetailsFetcher.$ // 使用 AccountDetails 的 Fetcher
                                .gender()
                                .phone()
                                .qq()
                                .wx()
                                .desc()
                                .address()
                );

        return sqlClient
                .createQuery(table)
                .where(table.id().eq(Long.valueOf(id)))
                .select(table.fetch(fetcher))
                .fetchOneOrNull();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("账号或密码错误");
        }
        return User
                .withUsername(username)
                .password(account.password())
                .roles(account.role())
                .build();
    }
}
