package org.example.cvitme01.controller.home;

import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.vo.response.MeterHomeVO;
import org.example.cvitme01.service.HomeMeterService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home/meters")
@RequiredArgsConstructor
public class MetersController {

     private final HomeMeterService homeMeterService;

     @GetMapping("/meter-self")
     public RestBean<List<MeterHomeVO>> getMeterSelf(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                                     @RequestParam("type") String type) {
         List<MeterHomeVO> meterSelf = homeMeterService.getMeterSelf(id, type);
         return RestBean.success(meterSelf);
     }

     @GetMapping("/meter-list")
        public RestBean<MeterHomeVO> getMeterListById(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                                            @RequestParam("meterId") int meterId) {
         MeterHomeVO meterList = homeMeterService.getMeterListById(id, meterId);
         return RestBean.success(meterList);
     }
}
