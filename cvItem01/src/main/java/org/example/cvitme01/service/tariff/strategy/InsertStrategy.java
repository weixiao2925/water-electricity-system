package org.example.cvitme01.service.tariff.strategy;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.TariffTier;
import org.example.cvitme01.entity.dto.TariffTierDraft;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsertStrategy implements TariffSaveStrategy {

    @Override
    public int execute(JSqlClient sqlClient, List<TariffTier> tariffs, long versionId) {
        if (tariffs.isEmpty()) return 0;

        List<TariffTier> insertDrafts = tariffs.stream()
                .map(t -> TariffTierDraft.$.produce(d -> {
                    d.setSeq(t.seq());
                    if (t.upperBound() != null) {
                        d.setUpperBound(t.upperBound());
                    }
                    d.setPrice(t.price());
                    d.setTariffVersionId(versionId);
                }))
                .toList();

        return sqlClient.getEntities()
                .saveEntitiesCommand(insertDrafts)
                .setMode(SaveMode.INSERT_ONLY)
                .execute()
                .getTotalAffectedRowCount();
    }
}
