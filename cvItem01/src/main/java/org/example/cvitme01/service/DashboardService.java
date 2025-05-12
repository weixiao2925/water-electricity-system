package org.example.cvitme01.service;

import org.example.cvitme01.entity.vo.response.ReadingSumVO;
import org.example.cvitme01.entity.vo.response.RecentReading;

import java.util.List;

public interface DashboardService {
    ReadingSumVO[] getSumReading(int id);
    List<RecentReading> getRecentReadings(int id);
}
