package org.example.cvitme01.service;

import org.example.cvitme01.entity.dto.TariffTier;
import org.example.cvitme01.entity.dto.TariffVersion;
import org.example.cvitme01.entity.vo.request.TariffTierSaveRequest;

import java.util.List;

public interface AdminTariffService {
    List<TariffTier> getTariffTiers();
    TariffVersion getNowVersion(String type);
    List<TariffVersion> getVersion(String type);
    String changeTariffVersion(String type, long oldId, long newId);
    String saveTariffTier(String type, long versionId, TariffTierSaveRequest request);
    String addTariffVersionAdd(TariffVersion version);
}
