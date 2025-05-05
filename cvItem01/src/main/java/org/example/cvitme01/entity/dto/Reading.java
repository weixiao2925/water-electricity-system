package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading")
public interface Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @ManyToOne
    @JoinColumn(name = "meter_id")
    Meter meter();

    LocalDateTime shotTime();

    BigDecimal value();

    BigDecimal delta();

    BigDecimal cost();

    String imageUrl();

    String previewUrl();
}
