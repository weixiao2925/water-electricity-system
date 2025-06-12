package org.example.cvitme01.service.tariff.operation;

import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;

@RequiredArgsConstructor
public abstract class AbstractTariffOperation<T> {

    protected final JSqlClient sqlClient;

    public final String execute(T request) {
        try {
            validateRequest(request);
            int affectedRows = performOperation(request);
            return processResult(affectedRows);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    protected abstract void validateRequest(T request);
    protected abstract int performOperation(T request);

    protected String processResult(int affectedRows) {
        return affectedRows > 0 ? null : "未知错误，请联系管理员";
    }

    protected String handleError(Exception e) {
        // 记录日志
        return "操作失败: " + e.getMessage();
    }
}
