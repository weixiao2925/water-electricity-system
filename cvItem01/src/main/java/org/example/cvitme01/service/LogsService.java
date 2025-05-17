package org.example.cvitme01.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.User;

public interface LogsService {
    void saveRequestLog(HttpServletRequest request, Object id, User user, JSONObject object);
    void saveRequestLog(HttpServletRequest request, JSONObject object);
    void saveResponseLog(int status, long time);
    void saveResponseLog(int status, long time, String body);
}
