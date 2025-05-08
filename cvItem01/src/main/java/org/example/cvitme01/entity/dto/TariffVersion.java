package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.sql.*;

import java.util.Date;

@Entity
@Table(name = "tariff_version")
public interface TariffVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    String type();

    String version();

    Date startTime();

    Date endTime();

    Boolean isActive();
}
