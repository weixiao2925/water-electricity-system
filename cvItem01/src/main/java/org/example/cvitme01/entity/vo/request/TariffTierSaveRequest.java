package org.example.cvitme01.entity.vo.request;

import lombok.Data;
import org.example.cvitme01.entity.dto.TariffTier;

import java.util.List;

@Data
public class TariffTierSaveRequest {
    private List<Long> deletedIds;
    private List<TariffTier> tariffTiers;
}
