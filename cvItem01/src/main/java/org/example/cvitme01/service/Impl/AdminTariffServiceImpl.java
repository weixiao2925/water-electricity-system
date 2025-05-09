package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.MutableUpdate;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.request.TariffTierSaveRequest;
import org.example.cvitme01.service.AdminTariffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public synchronized String saveTariffTier(
            String type, long versionId, TariffTierSaveRequest request) {

        Map<Boolean, List<TariffTier>> parts = request.getTariffTiers()
                .stream()
                .collect(Collectors.partitioningBy(t -> t.id() > 0));

        List<TariffTier> updateSource = parts.get(true);
        List<TariffTier> insertSource = parts.get(false);

        // 删
        List<Long> deleteIds = request.getDeletedIds();
        if (deleteIds != null && !deleteIds.isEmpty())
            sqlClient
                    .getEntities()
                    .deleteAll(TariffTier.class, deleteIds);

        // 改
        List<TariffTier> updateDrafts = updateSource.stream()
                .map(t -> TariffTierDraft.$.produce(d -> {
                    d.setId(t.id());
                    d.setSeq(t.seq());

                    if (t.upperBound() != null) {
                        d.setUpperBound(t.upperBound());
                    }

                    d.setPrice(t.price());
                }))
                .toList();

        int updateRows = 0;
        if (!updateDrafts.isEmpty()) {
            updateRows = sqlClient.getEntities()
                    .saveEntitiesCommand(updateDrafts)
                    .setMode(SaveMode.UPDATE_ONLY)
                    .execute()
                    .getTotalAffectedRowCount();
        }

        // 添
        List<TariffTier> insertDrafts = insertSource.stream()
                .map(t -> TariffTierDraft.$.produce(d -> {
                    d.setSeq(t.seq());
                    if (t.upperBound() != null) {
                        d.setUpperBound(t.upperBound());
                    }
                    d.setPrice(t.price());
                    d.setTariffVersionId(versionId);
                }))
                .toList();

        int insertRows = 0;
        if (!insertDrafts.isEmpty()) {
            insertRows = sqlClient.getEntities()
                    .saveEntitiesCommand(insertDrafts)
                    .setMode(SaveMode.INSERT_ONLY)
                    .execute()
                    .getTotalAffectedRowCount();
        }

        int total = insertRows + updateRows;
        return total > 0 ? null : "未知错误，请联系管理员";
    }


}
