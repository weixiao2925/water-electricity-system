package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

@Entity
@Table(name = "account_details")
public interface AccountDetails {

    @Id
    long id();

    short gender();

    String phone();

    String qq();

    String wx();

    String desc();

    String address();

}
