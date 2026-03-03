package org.example.cvitme01.service.strategy;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

public interface MeterReadingStrategy {
    JSONObject readMeter(HttpEntity<MultiValueMap<String, Object>> requestEntity);
    String getMeterType();
}
