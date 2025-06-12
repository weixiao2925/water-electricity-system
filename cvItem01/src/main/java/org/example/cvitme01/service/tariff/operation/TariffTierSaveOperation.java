package org.example.cvitme01.service.tariff.operation;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.TariffTier;
import org.example.cvitme01.service.tariff.context.TariffSaveContext;
import org.example.cvitme01.service.tariff.strategy.TariffSaveStrategyFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TariffTierSaveOperation extends AbstractTariffOperation<TariffSaveContext> {

    private final TariffSaveStrategyFactory strategyFactory;

    public TariffTierSaveOperation(JSqlClient sqlClient,
                                   TariffSaveStrategyFactory strategyFactory) {
        super(sqlClient);
        this.strategyFactory = strategyFactory;
    }

    @Override
    protected void validateRequest(TariffSaveContext context) {
        if (context.getRequest().getTariffTiers() == null) {
            throw new IllegalArgumentException("TariffTiers cannot be null");
        }
    }

    @Override
    protected int performOperation(TariffSaveContext context) {
        var request = context.getRequest();

        Map<Boolean, List<TariffTier>> parts = request.getTariffTiers()
                .stream()
                .collect(Collectors.partitioningBy(t -> t.id() > 0));

        // 删除操作
        int deleteRows = performDelete(request.getDeletedIds());

        // 更新操作
        int updateRows = strategyFactory.getStrategy(true)
                .execute(sqlClient, parts.get(true), context.getVersionId());

        // 插入操作
        int insertRows = strategyFactory.getStrategy(false)
                .execute(sqlClient, parts.get(false), context.getVersionId());

        return deleteRows + updateRows + insertRows;
    }

    private int performDelete(List<Long> deleteIds) {
        if (deleteIds == null || deleteIds.isEmpty()) return 0;

        return sqlClient.getEntities()
                .deleteAll(org.example.cvitme01.entity.dto.TariffTier.class, deleteIds)
                .getTotalAffectedRowCount();
    }
}
