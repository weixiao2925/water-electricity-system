package org.example.cvitme01.service;

import org.example.cvitme01.entity.dto.MonthlyBillSummary;

import java.util.List;

public interface BillsService {
    List<MonthlyBillSummary> getMonthlyBillSummary(long userId, String year);
}
