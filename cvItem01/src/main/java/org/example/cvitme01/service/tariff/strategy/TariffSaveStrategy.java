package org.example.cvitme01.service.tariff.strategy;

import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.TariffTier;

import java.util.List;

public interface TariffSaveStrategy {
    int execute(JSqlClient sqlClient, List<TariffTier> tariffs, long versionId);
}
