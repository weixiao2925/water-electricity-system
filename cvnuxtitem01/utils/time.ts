// 格式化日期时间
export const formatDateTime = (dateTimeStr: string) => {
    if (!dateTimeStr) return '-';

    try {
        const date = new Date(dateTimeStr);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    } catch (e) {
        return dateTimeStr; // 如果解析失败，返回原始字符串
    }
};
