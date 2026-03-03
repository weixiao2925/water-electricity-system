package org.example.cvitme01.service.factory;

import org.example.cvitme01.service.strategy.MeterReadingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MeterReadingStrategyFactory {

    private final Map<String, MeterReadingStrategy> strategies;

    public MeterReadingStrategyFactory(List<MeterReadingStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        MeterReadingStrategy::getMeterType,
                        Function.identity()
                ));
    }

    public MeterReadingStrategy getStrategy(String type) {
        MeterReadingStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported meter type: " + type);
        }
        return strategy;
    }
}
