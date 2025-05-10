package org.example.cvitme01.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingSumVO {
    private String type;
    private String unit;
    private BigDecimal current;
    private BigDecimal cost;
}
