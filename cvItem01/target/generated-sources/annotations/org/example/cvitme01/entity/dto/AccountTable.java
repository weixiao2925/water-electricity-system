package org.example.cvitme01.entity.dto;

import java.lang.Deprecated;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.Date;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.table.TableEx;
import org.babyfish.jimmer.sql.ast.table.spi.AbstractTypedTable;

@GeneratedBy(
        type = Account.class
)
public class AccountTable extends AbstractTypedTable<Account> implements AccountProps {
    public static final AccountTable $ = new AccountTable();

    public AccountTable() {
        super(Account.class);
    }

    public AccountTable(AbstractTypedTable.DelayedOperation<Account> delayedOperation) {
        super(Account.class, delayedOperation);
    }

    public AccountTable(TableImplementor<Account> table) {
        super(table);
    }

    protected AccountTable(AccountTable base, String joinDisabledReason) {
        super(base, joinDisabledReason);
    }

    @Override
    public PropExpression.Num<Long> id() {
        return __get(AccountProps.ID.unwrap());
    }

    @Override
    public PropExpression.Str username() {
        return __get(AccountProps.USERNAME.unwrap());
    }

    @Override
    public PropExpression.Str password() {
        return __get(AccountProps.PASSWORD.unwrap());
    }

    @Override
    public PropExpression.Str email() {
        return __get(AccountProps.EMAIL.unwrap());
    }

    @Override
    public PropExpression.Str role() {
        return __get(AccountProps.ROLE.unwrap());
    }

    @Override
    public PropExpression.Str avatar() {
        return __get(AccountProps.AVATAR.unwrap());
    }

    @Override
    public PropExpression.Dt<Date> registerTime() {
        return __get(AccountProps.REGISTER_TIME.unwrap());
    }

    @Override
    public AccountTableEx asTableEx() {
        return new AccountTableEx(this, null);
    }

    @Override
    public AccountTable __disableJoin(String reason) {
        return new AccountTable(this, reason);
    }

    @GeneratedBy(
            type = Account.class
    )
    public static class Remote extends AbstractTypedTable<Account> {
        public Remote(AbstractTypedTable.DelayedOperation delayedOperation) {
            super(Account.class, delayedOperation);
        }

        public Remote(TableImplementor<Account> table) {
            super(table);
        }

        public PropExpression.Num<Long> id() {
            return __get(AccountProps.ID.unwrap());
        }

        @Override
        @Deprecated
        public TableEx<Account> asTableEx() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Remote __disableJoin(String reason) {
            return this;
        }
    }
}
