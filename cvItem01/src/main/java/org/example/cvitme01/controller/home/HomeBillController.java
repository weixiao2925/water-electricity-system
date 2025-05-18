package org.example.cvitme01.controller.home;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.MonthlyBillSummary;
import org.example.cvitme01.service.BillsService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home/bill")
@RequiredArgsConstructor
public class HomeBillController {

    private final BillsService billsService;

    @GetMapping("/summary")
    public RestBean<List<MonthlyBillSummary>> getMonthlyBillSummary(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                                                    @RequestParam("year") String year) {
        return RestBean.success(billsService.getMonthlyBillSummary(id,year));
    }
}
