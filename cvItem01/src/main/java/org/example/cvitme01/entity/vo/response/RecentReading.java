package org.example.cvitme01.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentReading {
    private long id;
    private String type;
    private BigDecimal value;
    private LocalDateTime shotTime;
    private BigDecimal cost;
}
