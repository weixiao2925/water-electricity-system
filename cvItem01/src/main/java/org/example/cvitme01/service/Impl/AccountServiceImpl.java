package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.request.PasswordUpdateVO;
import org.example.cvitme01.repository.AccountRepository;
import org.example.cvitme01.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final JSqlClient sqlClient;
    private final PasswordEncoder passwordEncoder;

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
    public boolean updateAccountInfo(Integer id, Account account) {
        AccountTable table = AccountTable.$;

        MutableUpdate update = sqlClient
                .createUpdate(table)
                .set(table.username(), account.username());

        if (account.details() != null) {
            update = update
                    .set(table.details().gender(), account.details().gender())
                    .set(table.details().phone(), account.details().phone())
                    .set(table.details().qq(), account.details().qq())
                    .set(table.details().wx(), account.details().wx())
                    .set(table.details().desc(), account.details().desc())
                    .set(table.details().address(), account.details().address());
        }
        int affectedRows = update
                .where(table.id().eq(Long.valueOf(id)))
                .execute();
        return affectedRows > 0;
    }

    @Override
    public String updateAccountPassword(Integer id, PasswordUpdateVO vo) {
        AccountTable table = AccountTable.$;
        String password = sqlClient
                .createQuery(table)
                .where(table.id().eq(Long.valueOf(id)))
                .select(table.password())
                .fetchOneOrNull();

        if (!passwordEncoder.matches(vo.getOldPassword(), password))
            return "原密码错误,请重新输入";
        if (!vo.isValid())
            return "新密码不一致，请重新输入";

        return sqlClient
                .createUpdate(table)
                .set(table.password(), passwordEncoder.encode(vo.getNewPassword()))
                .where(table.id().eq(Long.valueOf(id)))
                .execute() > 0
                ? null
                : "未知错误，请联系管理员";
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
