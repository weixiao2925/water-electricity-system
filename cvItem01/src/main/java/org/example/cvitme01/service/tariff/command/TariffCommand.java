package org.example.cvitme01.service.tariff.command;

public interface TariffCommand {
    String execute();
    void undo(); // 可选的回滚操作
}
