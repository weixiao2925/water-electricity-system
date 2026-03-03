package org.example.cvitme01.service.calculator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.service.calculator.CostCalculator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseCostCalculator extends CostCalculator {

    private final JSqlClient sqlClient;

    @Override
    protected TierInfo getTierInfo(String type, int seq) {
        TariffTierTable table = TariffTierTable.$;
        Fetcher<TariffTier> fetcher = TariffTierFetcher.$
                .upperBound()
                .price();

        TariffTier tier = sqlClient.createQuery(table)
                .where(table.tariffVersion().type().eq(type))
                .where(table.tariffVersion().isActive().eq(true))
                .where(table.seq().eq((short)seq))
                .select(table.fetch(fetcher))
                .fetchOneOrNull();

        if (tier != null) {
            return new TierInfo(tier.upperBound(), tier.price());
        } else {
            log.error("没有找到对应的费用阶梯信息: type={}, seq={}", type, seq);
            return null;
        }
    }
}
