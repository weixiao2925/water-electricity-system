package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.service.AdminTariffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public synchronized String changeTariffVersion(String type, long oldId, long newId) {
        TariffVersionTable table = TariffVersionTable.$;

        MutableUpdate oldVersionUpdate = sqlClient
                .createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(oldId))
                .set(table.isActive(), Boolean.FALSE);
        MutableUpdate newVersionUpdate = sqlClient
                .createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(newId))
                .set(table.isActive(), Boolean.TRUE);

        int affectedRowsOld = oldVersionUpdate.execute();
        int affectedRowsNew = newVersionUpdate.execute();

        return affectedRowsOld > 0 && affectedRowsNew > 0
                ? null
                : "未知错误，请联系管理员";
    }
}
