package org.example.cvitme01.service.strategy.impl;

import com.alibaba.fastjson2.JSONObject;
import org.example.cvitme01.service.strategy.MeterReadingStrategy;
import org.example.cvitme01.utils.Const;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class WaterMeterReadingStrategy implements MeterReadingStrategy {

    private final RestTemplate restTemplate;

    public WaterMeterReadingStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public JSONObject readMeter(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        return restTemplate.postForObject(
                "http://localhost:5000/api/read_water_meter",
                requestEntity,
                JSONObject.class
        );
    }

    @Override
    public String getMeterType() {
        return Const.WATER;
    }
}
