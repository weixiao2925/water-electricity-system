package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "account_details")
public interface AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    short gender();

    @Nullable
    String phone();

    @Nullable
    String qq();

    @Nullable
    String wx();

    @Nullable
    @Column(name = "`desc`")
    String desc();

    @Nullable
    String address();

}
