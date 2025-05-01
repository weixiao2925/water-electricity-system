package org.example.cvitme01.entity.dto;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.impl.table.TableProxies;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.TableEx;
import org.babyfish.jimmer.sql.ast.table.WeakJoin;
import org.babyfish.jimmer.sql.ast.table.spi.AbstractTypedTable;

@GeneratedBy(
        type = Account.class
)
public class AccountTableEx extends AccountTable implements TableEx<Account> {
    public static final AccountTableEx $ = new AccountTableEx(AccountTable.$, null);

    public AccountTableEx() {
        super();
    }

    public AccountTableEx(AbstractTypedTable.DelayedOperation<Account> delayedOperation) {
        super(delayedOperation);
    }

    public AccountTableEx(TableImplementor<Account> table) {
        super(table);
    }

    protected AccountTableEx(AccountTable base, String joinDisabledReason) {
        super(base, joinDisabledReason);
    }

    public AccountDetailsTableEx details() {
        __beforeJoin();
        if (raw != null) {
            return new AccountDetailsTableEx(raw.joinImplementor(AccountProps.DETAILS.unwrap()));
        }
        return new AccountDetailsTableEx(joinOperation(AccountProps.DETAILS.unwrap()));
    }

    public AccountDetailsTableEx details(JoinType joinType) {
        __beforeJoin();
        if (raw != null) {
            return new AccountDetailsTableEx(raw.joinImplementor(AccountProps.DETAILS.unwrap(), joinType));
        }
        return new AccountDetailsTableEx(joinOperation(AccountProps.DETAILS.unwrap(), joinType));
    }

    @Override
    public AccountTableEx asTableEx() {
        return this;
    }

    @Override
    public AccountTableEx __disableJoin(String reason) {
        return new AccountTableEx(this, reason);
    }

    public <TT extends Table<?>, WJ extends WeakJoin<AccountTable, TT>> TT weakJoin(
            Class<WJ> weakJoinType) {
        return weakJoin(weakJoinType, JoinType.INNER);
    }

    @SuppressWarnings("all")
    public <TT extends Table<?>, WJ extends WeakJoin<AccountTable, TT>> TT weakJoin(
            Class<WJ> weakJoinType, JoinType joinType) {
        __beforeJoin();
        if (raw != null) {
            return (TT)TableProxies.wrap(raw.weakJoinImplementor(weakJoinType, joinType));
        }
        return (TT)TableProxies.fluent(joinOperation(weakJoinType, joinType));
    }
}
