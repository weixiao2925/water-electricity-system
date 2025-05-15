package org.example.cvitme01.service;

import org.example.cvitme01.entity.vo.response.MeterHomeVO;

import java.util.List;

public interface HomeMeterService {
    List<MeterHomeVO> getMeterSelf(int uid, String type);
    MeterHomeVO getMeterListById(int uid, int id);
}
