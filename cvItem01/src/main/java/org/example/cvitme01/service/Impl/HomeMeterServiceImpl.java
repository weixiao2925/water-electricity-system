package org.example.cvitme01.service.Impl;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.entity.vo.response.MeterSelfVO;
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
    public List<MeterSelfVO> getMeterSelf(int uid, String type) {
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
            MeterSelfVO meterSelfVO = new MeterSelfVO();
            meterSelfVO.setMeter(meter);
            meterSelfVO.setLastReading(getLastReading(meter));
            meterSelfVO.setUnit(getUnit(meter));
            meterSelfVO.setStatus("正常");
            return meterSelfVO;
        }).toList();
    }

    private BigDecimal getLastReading(Meter meter) {
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
        String type = meter.type();
        return switch (type) {
            case "water", "gas" -> "m³";
            case "electricity" -> "kWh";
            default -> null;
        };
    }
}
