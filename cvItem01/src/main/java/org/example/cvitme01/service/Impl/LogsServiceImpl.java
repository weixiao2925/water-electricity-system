package org.example.cvitme01.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.LogsDraft;
import org.example.cvitme01.service.LogsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService {

    private final JSqlClient sqlClient;

    @Override
    public void saveRequestLog(HttpServletRequest request, Object id, User user, JSONObject object) {
        sqlClient.getEntities().saveCommand(
                LogsDraft.$.produce(draft -> {
                    draft.setUrl(request.getServletPath());
                    draft.setHttpMethod(request.getMethod());
                    draft.setIp(request.getRemoteAddr());
                    draft.setUserId(Long.valueOf((Integer) id));
                    draft.setRole(user.getAuthorities().toString());
                    draft.setClassMethod(request.getServletPath());
                    draft.setParam(object.toJSONString());
                    draft.setCreateTime(new Date());
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();
    }

    @Override
    public void saveRequestLog(HttpServletRequest request, JSONObject object) {
        sqlClient.getEntities().saveCommand(
                LogsDraft.$.produce(draft -> {
                    draft.setUrl(request.getServletPath());
                    draft.setHttpMethod(request.getMethod());
                    draft.setIp(request.getRemoteAddr());
                    draft.setClassMethod(request.getServletPath());
                    draft.setParam(object.toJSONString());
                    draft.setCreateTime(new Date());
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();
    }

    @Override
    public void saveResponseLog(int status, long time) {
        sqlClient.getEntities().saveCommand(
                LogsDraft.$.produce(draft -> {
                    draft.setStatus(String.valueOf(status));
                    draft.setTimeConsuming(BigDecimal.valueOf(time));
                    draft.setCreateTime(new Date());
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();
    }

    @Override
    public void saveResponseLog(int status, long time, String body) {
        sqlClient.getEntities().saveCommand(
                LogsDraft.$.produce(draft -> {
                    draft.setStatus(String.valueOf(status));
                    draft.setTimeConsuming(BigDecimal.valueOf(time));
                    draft.setResult(body);
                    draft.setCreateTime(new Date());
                })
        ).setMode(SaveMode.INSERT_ONLY).execute();
    }
}
