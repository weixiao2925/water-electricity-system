package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.MonthlyBillSummary;
import org.example.cvitme01.entity.dto.MonthlyBillSummaryFetcher;
import org.example.cvitme01.entity.dto.MonthlyBillSummaryTable;
import org.example.cvitme01.service.BillsService;
import org.example.cvitme01.utils.Const;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillsServiceImpl implements BillsService {

    private final JSqlClient sqlClient;

    @Override
    public List<MonthlyBillSummary> getMonthlyBillSummary(long userId, String year) {
        MonthlyBillSummaryTable table = MonthlyBillSummaryTable.$;
        MonthlyBillSummaryFetcher fetcher = MonthlyBillSummaryFetcher.$
                .billMonth()
                .meterWater().totalWater()
                .meterElectricity().totalElectricity()
                .meterGas().totalGas()
                .totalCost()
                .status()
                .paid().paidDate();

        int yearInt = Integer.parseInt(year);
        String startMonth = LocalDate.of(yearInt, 1, 1).format(Const.MONTH_FORMATTER);
        String endMonth = LocalDate.of(yearInt, 12, 1).format(Const.MONTH_FORMATTER);

        return sqlClient
                .createQuery(table)
                .where(table.userId().eq(userId))
                .where(table.billMonth().between(startMonth, endMonth))
                .select(table.fetch(fetcher))
                .execute();
    }
}
