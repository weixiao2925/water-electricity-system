package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "monthly_bill_summary")
public interface MonthlyBillSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    @ManyToOne
    @JoinColumn(name = "user_id")
    Account user();

    @Key
    String billMonth();

    @Nullable
    BigDecimal meterWater();

    @Nullable
    BigDecimal meterElectricity();

    @Nullable
    BigDecimal meterGas();

    @Nullable
    BigDecimal totalWater();

    @Nullable
    BigDecimal totalElectricity();

    @Nullable
    BigDecimal totalGas();

    BigDecimal totalCost();

    String status();

    @Column(name = "is_paid")
    boolean isPaid();

    @Nullable
    Date paidDate();
}
