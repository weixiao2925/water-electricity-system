package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.component.EmailComponent;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.request.EmailRegisterVO;
import org.example.cvitme01.entity.vo.request.EmailResetVO;
import org.example.cvitme01.entity.vo.request.PasswordUpdateVO;
import org.example.cvitme01.repository.AccountRepository;
import org.example.cvitme01.service.AccountService;
import org.example.cvitme01.utils.Const;
import org.example.cvitme01.utils.RanMail;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final JSqlClient sqlClient;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailComponent emailComponent;

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
    public String registerEmailVerifyCode(String email) {
        RanMail ranMail = new RanMail();
        String code = ranMail.authCode();
        stringRedisTemplate.opsForValue()
                .set(Const.VERIFY_EMAIL_DATA + email, code, 5 * 60, TimeUnit.SECONDS);
        emailComponent.senEmail(email, "[校验]验证码(5分钟有效)", "验证码为： " + code);
        return null;
    }

    @Override
    @Transactional
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        String username = vo.getUsername();

        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误,请重新输入";
        if (this.existsAccountByUsernameOrEmail(username, email))
            return "用户名或邮箱已存在，请重新输入";

        String password = passwordEncoder.encode(vo.getPassword());

        var resultDetail = sqlClient.getEntities().saveCommand(
                AccountDetailsDraft.$.produce(draft -> {
                    draft.setGender((short) 1);
                    draft.setPhone(null);
                    draft.setQq(null);
                    draft.setWx(null);
                    draft.setDesc(null);
                    draft.setAddress(null);
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();

        if (resultDetail.getTotalAffectedRowCount() <= 0)
            return "未知错误：详情保存失败";

        long detailId = resultDetail.getModifiedEntity().id();

        var result = sqlClient.getEntities().saveCommand(
                AccountDraft.$.produce(draft -> {
                    draft.setUsername(username);
                    draft.setEmail(email);
                    draft.setPassword(password);
                    draft.setRole("user");
                    draft.setAvatar(null);
                    draft.setRegisterTime(new Date());
                    draft.setDetailsId(detailId);
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();

        if (result.getTotalAffectedRowCount() <= 0)
            return "未知错误：账号保存失败";

        this.deleteEmailCode(email);

        return null;
    }


    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        if (!this.existsAccountByEmail(email))
            return "邮箱不存在，请重新输入";

        String verify = this.resetConfirm(vo);
        if (verify != null) return verify;

        String password = passwordEncoder.encode(vo.getPassword());
        AccountTable table = AccountTable.$;
        int affectedRows = sqlClient
                .createUpdate(table)
                .where(table.email().eq(email))
                .set(table.password(), password)
                .execute();

        return affectedRows > 0
                ? null
                : "未知错误，请联系管理员";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            account = accountRepository.findByEmail(username);
            if (account == null) throw new UsernameNotFoundException("账号或密码错误");
        }
        return User
                .withUsername(account.username())
                .password(account.password())
                .roles(account.role())
                .build();
    }

    private boolean existsAccountByUsernameOrEmail(String username, String email) {
        AccountTable table = AccountTable.$;

        Predicate condition = Predicate.or(
                table.username().eq(username),
                table.email().eq(email)
        );

        return !sqlClient.createQuery(table)
                .where(condition)
                .select(table.id())
                .limit(1) // 提高性能
                .execute()
                .isEmpty();
    }



    private boolean existsAccountByEmail(String email) {
        AccountTable table = AccountTable.$;
        List<Long> execute = sqlClient.createQuery(table)
                .where(table.email().eq(email))
                .select(table.id())
                .execute();
        return !execute.isEmpty();
    }

    private void deleteEmailCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    private String resetConfirm(EmailResetVO vo) {
        String email = vo.getEmail();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误,请重新输入";
        return null;
    }
}
