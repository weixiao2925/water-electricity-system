package org.example.cvitme01.entity.dto;

import java.lang.Long;
import java.lang.String;
import java.util.Date;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.table.Props;
import org.babyfish.jimmer.sql.ast.table.PropsFor;

@GeneratedBy(
        type = Account.class
)
@PropsFor(Account.class)
public interface AccountProps extends Props {
    TypedProp.Scalar<Account, Long> ID = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("id"));

    TypedProp.Scalar<Account, String> USERNAME = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("username"));

    TypedProp.Scalar<Account, String> PASSWORD = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("password"));

    TypedProp.Scalar<Account, String> EMAIL = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("email"));

    TypedProp.Scalar<Account, String> ROLE = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("role"));

    TypedProp.Scalar<Account, String> AVATAR = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("avatar"));

    TypedProp.Scalar<Account, Date> REGISTER_TIME = 
        TypedProp.scalar(ImmutableType.get(Account.class).getProp("registerTime"));

    PropExpression.Num<Long> id();

    PropExpression.Str username();

    PropExpression.Str password();

    PropExpression.Str email();

    PropExpression.Str role();

    PropExpression.Str avatar();

    PropExpression.Dt<Date> registerTime();
}
