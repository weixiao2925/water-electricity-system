package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Entity
@Table(name = "account")
public interface Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String username();

    String password();

    String email();

    String role();

    @Nullable
    String avatar();

    Date registerTime();

    @OneToOne
    @JoinColumn(name = "details_id")
    @Nullable
    AccountDetails details();
}
