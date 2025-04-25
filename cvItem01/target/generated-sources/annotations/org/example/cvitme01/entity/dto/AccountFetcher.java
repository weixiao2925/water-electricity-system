package org.example.cvitme01.entity.dto;

import java.lang.Override;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.lang.NewChain;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.babyfish.jimmer.sql.fetcher.FieldConfig;
import org.babyfish.jimmer.sql.fetcher.IdOnlyFetchType;
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl;
import org.babyfish.jimmer.sql.fetcher.spi.AbstractTypedFetcher;

@GeneratedBy(
        type = Account.class
)
public class AccountFetcher extends AbstractTypedFetcher<Account, AccountFetcher> {
    public static final AccountFetcher $ = new AccountFetcher(null);

    private AccountFetcher(FetcherImpl<Account> base) {
        super(Account.class, base);
    }

    private AccountFetcher(AccountFetcher prev, ImmutableProp prop, boolean negative,
            IdOnlyFetchType idOnlyFetchType) {
        super(prev, prop, negative, idOnlyFetchType);
    }

    private AccountFetcher(AccountFetcher prev, ImmutableProp prop,
            FieldConfig<?, ? extends Table<?>> fieldConfig) {
        super(prev, prop, fieldConfig);
    }

    public static AccountFetcher $from(Fetcher<Account> base) {
        return base instanceof AccountFetcher ? 
        	(AccountFetcher)base : 
        	new AccountFetcher((FetcherImpl<Account>)base);
    }

    @NewChain
    public AccountFetcher username() {
        return add("username");
    }

    @NewChain
    public AccountFetcher username(boolean enabled) {
        return enabled ? add("username") : remove("username");
    }

    @NewChain
    public AccountFetcher password() {
        return add("password");
    }

    @NewChain
    public AccountFetcher password(boolean enabled) {
        return enabled ? add("password") : remove("password");
    }

    @NewChain
    public AccountFetcher email() {
        return add("email");
    }

    @NewChain
    public AccountFetcher email(boolean enabled) {
        return enabled ? add("email") : remove("email");
    }

    @NewChain
    public AccountFetcher role() {
        return add("role");
    }

    @NewChain
    public AccountFetcher role(boolean enabled) {
        return enabled ? add("role") : remove("role");
    }

    @NewChain
    public AccountFetcher avatar() {
        return add("avatar");
    }

    @NewChain
    public AccountFetcher avatar(boolean enabled) {
        return enabled ? add("avatar") : remove("avatar");
    }

    @NewChain
    public AccountFetcher registerTime() {
        return add("registerTime");
    }

    @NewChain
    public AccountFetcher registerTime(boolean enabled) {
        return enabled ? add("registerTime") : remove("registerTime");
    }

    @Override
    protected AccountFetcher createFetcher(ImmutableProp prop, boolean negative,
            IdOnlyFetchType idOnlyFetchType) {
        return new AccountFetcher(this, prop, negative, idOnlyFetchType);
    }

    @Override
    protected AccountFetcher createFetcher(ImmutableProp prop,
            FieldConfig<?, ? extends Table<?>> fieldConfig) {
        return new AccountFetcher(this, prop, fieldConfig);
    }
}
