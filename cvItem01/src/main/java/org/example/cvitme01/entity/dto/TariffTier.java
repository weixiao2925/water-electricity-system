package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@Entity
@Table(name = "tariff_tier")
public interface TariffTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    short seq();

    @Nullable
    BigDecimal upperBound();

    BigDecimal price();

    @Key
    @ManyToOne
    @JoinColumn(name = "version_id")
    TariffVersion tariffVersion();

}
