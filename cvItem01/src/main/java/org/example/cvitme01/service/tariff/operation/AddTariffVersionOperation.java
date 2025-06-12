package org.example.cvitme01.service.tariff.operation;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.TariffVersion;
import org.example.cvitme01.entity.dto.TariffVersionDraft;
import org.springframework.stereotype.Component;

@Component
public class AddTariffVersionOperation extends AbstractTariffOperation<TariffVersion> {

    public AddTariffVersionOperation(JSqlClient sqlClient) {
        super(sqlClient);
    }

    @Override
    protected void validateRequest(TariffVersion version) {
        if (version.type() == null || version.version() == null) {
            throw new IllegalArgumentException("Type and version cannot be null");
        }
    }

    @Override
    protected int performOperation(TariffVersion version) {
        var result = sqlClient.getEntities().saveCommand(
                TariffVersionDraft.$.produce(draft -> {
                    draft.setType(version.type());
                    draft.setVersion(version.version());
                    draft.setStartTime(version.startTime());
                    draft.setEndTime(version.endTime());
                    draft.setIsActive(false);
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();

        return result.getTotalAffectedRowCount();
    }
}
