package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.query.ConfigurableRootQuery;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.request.TariffTierSaveRequest;
import org.example.cvitme01.service.AdminTariffService;
import org.example.cvitme01.service.tariff.builder.TariffFetcherBuilder;
import org.example.cvitme01.service.tariff.command.ChangeTariffVersionCommand;
import org.example.cvitme01.service.tariff.context.TariffSaveContext;
import org.example.cvitme01.service.tariff.operation.AddTariffVersionOperation;
import org.example.cvitme01.service.tariff.operation.TariffTierSaveOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTariffServiceImpl implements AdminTariffService {

    private final JSqlClient sqlClient;
    private final TariffFetcherBuilder fetcherBuilder;
    private final TariffTierSaveOperation tariffTierSaveOperation;
    private final AddTariffVersionOperation addTariffVersionOperation;
    private final ChangeTariffVersionCommand changeTariffVersionCommand;

    @Override
    public List<TariffTier> getTariffTiers() {
        TariffTierTable table = TariffTierTable.$;
        Fetcher<TariffTier> fetcher = fetcherBuilder
                .tariffTier()
                .withSeq()
                .withUpperBound()
                .withPrice()
                .withTariffVersion()
                .build();

        return sqlClient
                .createQuery(table)
                .select(table.fetch(fetcher))
                .execute();
    }

    @Override
    public TariffVersion getNowVersion(String type) {
        TariffVersionTable table = TariffVersionTable.$;
        Fetcher<TariffVersion> fetcher = fetcherBuilder
                .tariffVersion()
                .withVersion()
                .withStartTime()
                .build();

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
        Fetcher<TariffVersion> fetcher = fetcherBuilder
                .tariffVersion()
                .withVersion()
                .withStartTime()
                .build();

        return sqlClient
                .createQuery(table)
                .where(table.type().eq(type))
                .select(table.fetch(fetcher))
                .execute();
    }

    @Override
    @Transactional
    public synchronized String changeTariffVersion(String type, long oldId, long newId) {
        return changeTariffVersionCommand
                .configure(type, oldId, newId)
                .execute();
    }

    @Override
    @Transactional
    public synchronized String saveTariffTier(String type, long versionId,
                                              TariffTierSaveRequest request) {
        return tariffTierSaveOperation
                .execute(new TariffSaveContext(type, versionId, request));
    }

    @Override
    public String addTariffVersionAdd(TariffVersion version) {
        return addTariffVersionOperation.execute(version);
    }
}
