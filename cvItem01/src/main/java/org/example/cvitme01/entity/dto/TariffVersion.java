package org.example.cvitme01.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date startTime();

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date endTime();

    Boolean isActive();
}
