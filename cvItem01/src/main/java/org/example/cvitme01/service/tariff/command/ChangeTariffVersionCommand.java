package org.example.cvitme01.service.tariff.command;

import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.TariffVersionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChangeTariffVersionCommand implements TariffCommand {

    @Autowired
    private JSqlClient sqlClient;

    private String type;
    private long oldId;
    private long newId;

    public ChangeTariffVersionCommand configure(String type, long oldId, long newId) {
        this.type = type;
        this.oldId = oldId;
        this.newId = newId;
        return this;
    }

    @Override
    @Transactional
    public synchronized String execute() {
        TariffVersionTable table = TariffVersionTable.$;

        int affectedRowsOld = sqlClient
                .createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(oldId))
                .set(table.isActive(), Boolean.FALSE)
                .execute();

        int affectedRowsNew = sqlClient
                .createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(newId))
                .set(table.isActive(), Boolean.TRUE)
                .execute();

        return affectedRowsOld > 0 && affectedRowsNew > 0
                ? null
                : "未知错误，请联系管理员";
    }

    @Override
    public void undo() {
        // 回滚逻辑
        TariffVersionTable table = TariffVersionTable.$;
        sqlClient.createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(oldId))
                .set(table.isActive(), Boolean.TRUE)
                .execute();

        sqlClient.createUpdate(table)
                .where(table.type().eq(type))
                .where(table.id().eq(newId))
                .set(table.isActive(), Boolean.FALSE)
                .execute();
    }
}
