package org.example.cvitme01.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cvitme01.entity.dto.Meter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeterSelfVO {
    private Meter meter;
    private BigDecimal lastReading;
    private String unit;
    private String status;
}
