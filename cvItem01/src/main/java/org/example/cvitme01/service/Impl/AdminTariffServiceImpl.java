package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.service.AdminTariffService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTariffServiceImpl implements AdminTariffService {

    private final JSqlClient sqlClient;

    @Override
    public List<TariffTier> getTariffTiers() {
        TariffTierTable table = TariffTierTable.$;
        Fetcher<TariffTier> fetcher = TariffTierFetcher.$
                .seq()
                .upperBound()
                .price()
                .tariffVersion(
                        TariffVersionFetcher.$
                                .type()
                                .version()
                                .startTime()
                                .endTime()
                                .isActive()
                );
        return sqlClient
                .createQuery(table)
                .select(table.fetch(fetcher))
                .execute();
    }

    @Override
    public String addTariffTier(TariffTier tariffTier) {
        return "";
    }

    @Override
    public TariffVersion getNowVersion(String type) {
        TariffVersionTable table = TariffVersionTable.$;
        Fetcher<TariffVersion> fetcher = TariffVersionFetcher.$
                .version()
                .startTime();
        return sqlClient
                .createQuery(table)
                .where(table.type().eq(type))
                .where(table.isActive().eq(Boolean.TRUE))
                .select(table.fetch(fetcher))
                .fetchOneOrNull();
    }

    @Override
    public List<TariffVersion> getVersion(String type) {
        TariffVersionTable table = TariffVersionTable.$;
        Fetcher<TariffVersion> fetcher = TariffVersionFetcher.$
                .version()
                .startTime();
        return sqlClient
                .createQuery(table)
                .where(table.type().eq(type))
                .select(table.fetch(fetcher))
                .execute();
    }
}
