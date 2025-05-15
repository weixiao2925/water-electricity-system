package org.example.cvitme01.service;

import org.example.cvitme01.entity.vo.response.MeterSelfVO;

import java.util.List;

public interface HomeMeterService {
    List<MeterSelfVO> getMeterSelf(int uid, String type);
}
