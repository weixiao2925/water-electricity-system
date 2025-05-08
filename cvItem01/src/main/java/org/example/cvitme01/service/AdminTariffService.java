package org.example.cvitme01.service;

import org.example.cvitme01.entity.dto.TariffTier;
import org.example.cvitme01.entity.dto.TariffVersion;

import java.util.List;

public interface AdminTariffService {
    List<TariffTier> getTariffTiers();
    String addTariffTier(TariffTier tariffTier);
    TariffVersion getNowVersion(String type);
    List<TariffVersion> getVersion(String type);
}
