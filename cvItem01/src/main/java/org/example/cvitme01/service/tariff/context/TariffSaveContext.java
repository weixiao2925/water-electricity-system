package org.example.cvitme01.service.tariff.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.cvitme01.entity.vo.request.TariffTierSaveRequest;

@Data
@AllArgsConstructor
public class TariffSaveContext {
    private String type;
    private long versionId;
    private TariffTierSaveRequest request;
}
