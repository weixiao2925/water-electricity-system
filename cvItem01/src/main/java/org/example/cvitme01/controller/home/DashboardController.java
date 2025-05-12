package org.example.cvitme01.controller.home;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.vo.response.ReadingSumVO;
import org.example.cvitme01.entity.vo.response.RecentReading;
import org.example.cvitme01.service.DashboardService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sum")
    public RestBean<ReadingSumVO[]> sumList(@RequestAttribute(Const.ATTR_USER_ID) int id){
        ReadingSumVO[] readingSumVOS = dashboardService.getSumReading(id);
        return RestBean.success(readingSumVOS);
    }

    @GetMapping("/recent")
    public RestBean<List<RecentReading>> recentList(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        return RestBean.success(dashboardService.getRecentReadings(id));
    }

}
