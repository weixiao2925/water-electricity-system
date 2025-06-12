package org.example.cvitme01.service.tariff.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TariffSaveStrategyFactory {

    private final InsertStrategy insertStrategy;
    private final UpdateStrategy updateStrategy;

    public TariffSaveStrategy getStrategy(boolean isUpdate) {
        return isUpdate ? updateStrategy : insertStrategy;
    }
}
