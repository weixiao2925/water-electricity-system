package org.example.cvitme01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeterManagementService {

    private final JSqlClient sqlClient;

    public long getOrCreateMeter(String selectType, String location, int accountId) {
        MeterTable meterTable = MeterTable.$;
        Optional<Meter> existingMeterOpt = sqlClient.createQuery(meterTable)
                .where(meterTable.type().eq(selectType))
                .where(meterTable.location().eq(location))
                .where(meterTable.account().getId().eq(accountId))
                .select(meterTable)
                .fetchOptional();

        if (existingMeterOpt.isPresent()) {
            long meterId = existingMeterOpt.get().id();
            log.info("找到已存在的位置，ID: {}, 位置: {}", meterId, location);
            return meterId;
        } else {
            return createNewMeter(selectType, location, accountId);
        }
    }

    private long createNewMeter(String selectType, String location, int accountId) {
        log.info("未找到位置为 '{}' 的仪表，为用户 ID {} 创建新仪表", location, accountId);
        Meter newMeter = MeterDraft.$.produce(draft -> {
            draft.setLocation(location);
            draft.setType(selectType);
            draft.applyAccount(acc -> acc.setId(accountId));
        });

        var result = sqlClient.getEntities().saveCommand(newMeter)
                .setMode(SaveMode.INSERT_ONLY)
                .execute();

        if (result.getTotalAffectedRowCount() <= 0) {
            throw new RuntimeException("创建新仪表失败");
        }

        long meterId = result.getModifiedEntity().id();
        log.info("新仪表创建成功，ID: {}", meterId);
        return meterId;
    }
}
