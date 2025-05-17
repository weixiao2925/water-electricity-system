package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "logs")
public interface Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Nullable
    String url();

    @Nullable
    String param();

    @Nullable
    String status();

    @Nullable
    String httpMethod();

    @Nullable
    String ip();

    @Nullable
    String classMethod();

    @Nullable
    String role();

    @Key
    @ManyToOne
    @Nullable
    @JoinColumn(name = "user_id")
    Account user();

    @Nullable
    String version();

    @Nullable
    String result();

    @Nullable
    BigDecimal timeConsuming();

    @Nullable
    Date createTime();

}
