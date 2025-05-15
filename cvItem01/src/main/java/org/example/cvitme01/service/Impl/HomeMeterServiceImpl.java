package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.response.MeterHomeVO;
import org.example.cvitme01.service.HomeMeterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeMeterServiceImpl implements HomeMeterService {

    private final JSqlClient sqlClient;


    @Override
    @Transactional
    public List<MeterHomeVO> getMeterSelf(int uid, String type) {
        MeterTable table = MeterTable.$;
        MeterFetcher fetcher = MeterFetcher.$
                .type()
                .location()
                .installDate();

        List<Meter> meters = sqlClient.createQuery(table)
                .where(table.account().id().eq((long) uid))
                .whereIf(type.equals("water") || type.equals("electricity") || type.equals("gas"),
                        () -> table.type().eq(type))
                .select(table.fetch(fetcher))
                .execute();

        return meters.stream().map(meter -> {
            MeterHomeVO meterHomeVO = new MeterHomeVO();
            meterHomeVO.setMeter(meter);
            meterHomeVO.setLastReading(getLastReading(meter));
            meterHomeVO.setUnit(getUnit(meter));
            meterHomeVO.setStatus("正常");
            return meterHomeVO;
        }).toList();
    }

    @Override
    @Transactional
    public MeterHomeVO getMeterListById(int uid, int id) {
        MeterTable table = MeterTable.$;
        MeterFetcher fetcher = MeterFetcher.$
                .type()
                .location()
                .installDate()
                .readings(
                        ReadingFetcher.$
                                .shotTime()
                                .value()
                                .cost()
                );

        Meter meter = sqlClient.createQuery(table)
                .where(table.account().id().eq((long) uid))
                .where(table.id().eq((long) id))
                .select(table.fetch(fetcher))
                .fetchOneOrNull();
        return new MeterHomeVO(
                meter,
                getLastReading(meter),
                getUnit(meter),
                "正常"
        );
    }

    private BigDecimal getLastReading(Meter meter) {
        if (meter == null) return null;

        ReadingTable table = ReadingTable.$;
        ReadingFetcher fetcher = ReadingFetcher.$
                .value();

        Reading reading = sqlClient.createQuery(table)
                .where(table.meter().id().eq(meter.id()))
                .orderBy(table.shotTime().desc())
                .select(table.fetch(fetcher))
                .fetchFirst();

        if (reading == null) return null;

        return reading.value();
    }

    private String getUnit(Meter meter) {
        if (meter == null) return null;

        String type = meter.type();
        return switch (type) {
            case "water", "gas" -> "m³";
            case "electricity" -> "kWh";
            default -> null;
        };
    }
}
