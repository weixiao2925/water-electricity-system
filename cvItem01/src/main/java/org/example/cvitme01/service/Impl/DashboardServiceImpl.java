package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.MeterFetcher;
import org.example.cvitme01.entity.dto.Reading;
import org.example.cvitme01.entity.dto.ReadingFetcher;
import org.example.cvitme01.entity.dto.ReadingTable;
import org.example.cvitme01.entity.vo.response.ReadingSumVO;
import org.example.cvitme01.entity.vo.response.RecentReading;
import org.example.cvitme01.service.DashboardService;
import org.example.cvitme01.utils.Const;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final JSqlClient sqlClient;

    @Override
    public ReadingSumVO[] getSumReading() {
        ReadingTable table = ReadingTable.$;
        ReadingFetcher fetcher = ReadingFetcher.$
                .value()
                .cost();

        List<Reading> waterReadings = sqlClient.createQuery(table)
                .where(table.meter().type().eq(Const.WATER))
                .select(table.fetch(fetcher))
                .execute();

        List<Reading> electricityReadings = sqlClient.createQuery(table)
                .where(table.meter().type().eq(Const.ELECTRICITY))
                .select(table.fetch(fetcher))
                .execute();

        List<Reading> gasReadings = sqlClient.createQuery(table)
                .where(table.meter().type().eq(Const.GAS))
                .select(table.fetch(fetcher))
                .execute();

        return new ReadingSumVO[]{
                sumOfReadings(Const.WATER, "吨", waterReadings),
                sumOfReadings(Const.ELECTRICITY, "度", electricityReadings),
                sumOfReadings(Const.GAS, "立方", gasReadings)
        };
    }

    @Override
    public List<RecentReading> getRecentReadings() {
        ReadingTable table = ReadingTable.$;
        ReadingFetcher fetcher = ReadingFetcher.$
                .value()
                .shotTime()
                .cost()
                .meter(
                        MeterFetcher.$
                                .type()
                );

        List<Reading> readings = sqlClient.createQuery(table)
                .orderBy(table.shotTime().desc())
                .select(table.fetch(fetcher))
                .limit(5)
                .execute();

        return readings.stream().map(r -> new RecentReading(
                r.id(),
                r.meter().type(),
                r.value(),
                r.shotTime(),
                r.cost()
        )).toList();
    }


    private ReadingSumVO sumOfReadings(String type, String unit, List<Reading> readings) {
        BigDecimal current = readings.stream()
                .map(Reading::value)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cost = readings.stream()
                .map(Reading::cost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReadingSumVO(type, unit, current, cost);
    }

}
