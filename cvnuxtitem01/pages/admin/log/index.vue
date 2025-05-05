<template>
    <NuxtLayout>
        <div class="logs-page">
            <h1 class="page-title">系统日志</h1>

            <!-- 日志类型切换 -->
            <div class="log-type-tabs">
                <button
                    class="tab-btn"
                    :class="{ active: logType === 'operation' }"
                    @click="switchLogType('operation')">
                    操作日志
                </button>
                <button
                    class="tab-btn"
                    :class="{ active: logType === 'system' }"
                    @click="switchLogType('system')">
                    系统日志
                </button>
            </div>

            <!-- 筛选区域 -->
            <div class="filter-section">
                <div class="filter-row">
                    <div class="filter-item">
                        <span>日志级别：</span>
                        <select v-model="filters.level" class="filter-select">
                            <option value="">全部</option>
                            <option value="info">信息</option>
                            <option value="warning">警告</option>
                            <option value="error">错误</option>
                            <option value="critical">严重</option>
                        </select>
                    </div>
                    <div class="filter-item" v-if="logType === 'operation'">
                        <span>操作用户：</span>
                        <input type="text" v-model="filters.username" class="filter-input" placeholder="用户名/ID">
                    </div>
                    <div class="filter-item" v-if="logType === 'operation'">
                        <span>操作类型：</span>
                        <select v-model="filters.actionType" class="filter-select">
                            <option value="">全部</option>
                            <option value="login">登录</option>
                            <option value="logout">登出</option>
                            <option value="create">创建</option>
                            <option value="update">更新</option>
                            <option value="delete">删除</option>
                            <option value="export">导出</option>
                            <option value="import">导入</option>
                        </select>
                    </div>
                    <div class="filter-item" v-if="logType === 'system'">
                        <span>服务模块：</span>
                        <select v-model="filters.module" class="filter-select">
                            <option value="">全部</option>
                            <option value="auth">认证服务</option>
                            <option value="billing">计费服务</option>
                            <option value="ocr">OCR服务</option>
                            <option value="notification">通知服务</option>
                            <option value="storage">存储服务</option>
                        </select>
                    </div>
                </div>
                <div class="filter-row">
                    <div class="filter-item">
                        <span>时间范围：</span>
                        <input type="date" v-model="filters.startDate" class="date-input">
                        <span class="to-text">至</span>
                        <input type="date" v-model="filters.endDate" class="date-input">
                    </div>
                    <div class="filter-item">
                        <span>关键词：</span>
                        <input type="text" v-model="filters.keyword" class="filter-input" placeholder="日志内容关键词">
                    </div>
                    <div class="filter-actions">
                        <button class="btn btn-primary" @click="searchLogs">查询</button>
                        <button class="btn btn-default" @click="resetFilters">重置</button>
                    </div>
                </div>
            </div>

            <!-- 导出按钮 -->
            <div class="export-section">
                <button class="btn btn-export" @click="exportToCSV">
                    <i class="export-icon">↓</i> 导出CSV
                </button>
            </div>

            <!-- 日志表格 -->
            <div class="log-table-wrapper">
                <table class="log-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>时间</th>
                            <th>级别</th>
                            <th v-if="logType === 'operation'">用户</th>
                            <th v-if="logType === 'operation'">操作类型</th>
                            <th v-if="logType === 'operation'">操作对象</th>
                            <th v-if="logType === 'system'">服务模块</th>
                            <th v-if="logType === 'system'">IP地址</th>
                            <th class="content-column">日志内容</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="log in logs" :key="log.id" :class="'level-' + log.level">
                            <td>{{ log.id }}</td>
                            <td>{{ formatDate(log.timestamp) }}</td>
                            <td>
                                <span class="log-level" :class="'level-' + log.level">
                                    {{ getLevelText(log.level) }}
                                </span>
                            </td>
                            <td v-if="logType === 'operation'">{{ log.username }}</td>
                            <td v-if="logType === 'operation'">{{ getActionTypeText(log.actionType) }}</td>
                            <td v-if="logType === 'operation'">{{ log.targetObject }}</td>
                            <td v-if="logType === 'system'">{{ getModuleText(log.module) }}</td>
                            <td v-if="logType === 'system'">{{ log.ipAddress }}</td>
                            <td class="log-content">{{ log.content }}</td>
                        </tr>
                        <tr v-if="logs.length === 0">
                            <td :colspan="logType === 'operation' ? 7 : 6" class="empty-data">
                                暂无日志数据
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 分页控件 -->
            <div class="pagination">
                <button :disabled="currentPage <= 1" @click="changePage(currentPage - 1)" class="page-btn">
                    上一页
                </button>
                <div class="page-numbers">
                    <button
                        v-for="page in displayedPages"
                        :key="page"
                        :class="{ active: currentPage === page }"
                        @click="changePage(page)"
                        class="page-number">
                        {{ page }}
                    </button>
                </div>
                <button :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)" class="page-btn">
                    下一页
                </button>
                <span class="page-info">共 {{ totalItems }} 条记录，{{ totalPages }} 页</span>
            </div>
        </div>
    </NuxtLayout>
</template>

<script setup>

definePageMeta({
    layout: 'admin'
});

// 日志类型 (操作日志/系统日志)
const logType = ref('operation');

// 筛选条件
const filters = ref({
    level: '',
    username: '',
    actionType: '',
    module: '',
    startDate: '',
    endDate: '',
    keyword: ''
});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

// 分页展示
const displayedPages = computed(() => {
    const pages = [];
    const max = Math.min(totalPages.value, 7);

    if (totalPages.value <= 7) {
        // 总页数少于7，直接显示所有页码
        for (let i = 1; i <= totalPages.value; i++) {
            pages.push(i);
        }
    } else {
        // 总页数大于7，显示部分页码
        if (currentPage.value <= 4) {
            // 当前页在前4页
            for (let i = 1; i <= 5; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(totalPages.value);
        } else if (currentPage.value >= totalPages.value - 3) {
            // 当前页在后4页
            pages.push(1);
            pages.push('...');
            for (let i = totalPages.value - 4; i <= totalPages.value; i++) {
                pages.push(i);
            }
        } else {
            // 当前页在中间
            pages.push(1);
            pages.push('...');
            for (let i = currentPage.value - 1; i <= currentPage.value + 1; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(totalPages.value);
        }
    }

    return pages;
});

// 日志数据
const logs = ref([]);

// 监听日志类型变化，重置页面
watch(logType, () => {
    resetFilters();
    loadLogs();
});

// 初始化
onMounted(() => {
    loadLogs();
});

// 切换日志类型
function switchLogType(type) {
    logType.value = type;
}

// 重置筛选条件
function resetFilters() {
    filters.value = {
        level: '',
        username: '',
        actionType: '',
        module: '',
        startDate: '',
        endDate: '',
        keyword: ''
    };
    currentPage.value = 1;
}

// 搜索日志
function searchLogs() {
    currentPage.value = 1;
    loadLogs();
}

// 加载日志数据
function loadLogs() {
    // 模拟API调用，实际项目中应替换为真实API请求
    setTimeout(() => {
        const mockData = generateMockLogs();
        logs.value = mockData.logs;
        totalItems.value = mockData.total;
    }, 300);
}

// 生成模拟日志数据
function generateMockLogs() {
    const total = logType.value === 'operation' ? 247 : 389;
    let filteredTotal = total;

    // 模拟筛选逻辑
    if (filters.value.level || filters.value.username || filters.value.actionType ||
        filters.value.module || filters.value.keyword || filters.value.startDate) {
        filteredTotal = Math.floor(total * 0.4);
    }

    const mockLogs = [];
    const start = (currentPage.value - 1) * pageSize.value;
    const end = Math.min(start + pageSize.value, filteredTotal);

    const levels = ['info', 'warning', 'error', 'critical'];
    const actionTypes = ['login', 'logout', 'create', 'update', 'delete', 'export', 'import'];
    const modules = ['auth', 'billing', 'ocr', 'notification', 'storage'];
    const users = ['admin', 'operator', 'manager', 'user1', 'user2'];

    for (let i = start; i < end; i++) {
        const timestamp = new Date(Date.now() - Math.random() * 30 * 86400000);
        const level = filters.value.level || levels[Math.floor(Math.random() * levels.length)];

        if (logType.value === 'operation') {
            const actionType = filters.value.actionType || actionTypes[Math.floor(Math.random() * actionTypes.length)];
            const username = filters.value.username || users[Math.floor(Math.random() * users.length)];

            let content = '';
            let targetObject = '';

            switch (actionType) {
                case 'login':
                    content = `用户 ${username} 成功登录系统`;
                    targetObject = 'System';
                    break;
                case 'logout':
                    content = `用户 ${username} 退出登录`;
                    targetObject = 'System';
                    break;
                case 'create':
                    targetObject = `METER-${1000 + Math.floor(Math.random() * 100)}`;
                    content = `创建了新的表计 ${targetObject}`;
                    break;
                case 'update':
                    targetObject = `USER-${2000 + Math.floor(Math.random() * 100)}`;
                    content = `更新了用户信息 ${targetObject}`;
                    break;
                case 'delete':
                    targetObject = `TASK-${3000 + Math.floor(Math.random() * 100)}`;
                    content = `删除了OCR任务 ${targetObject}`;
                    break;
                case 'export':
                    targetObject = 'Bills';
                    content = `导出了账单数据`;
                    break;
                case 'import':
                    targetObject = 'Meters';
                    content = `批量导入了表计数据`;
                    break;
            }

            mockLogs.push({
                id: `OL-${100000 + i}`,
                timestamp,
                level,
                username,
                actionType,
                targetObject,
                content
            });
        } else {
            const module = filters.value.module || modules[Math.floor(Math.random() * modules.length)];
            const ipAddress = `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`;

            let content = '';

            switch (module) {
                case 'auth':
                    content = level === 'error'
                        ? '认证服务异常：多次失败的登录尝试'
                        : '认证服务正常运行中';
                    break;
                case 'billing':
                    content = level === 'warning'
                        ? '计费服务警告：账单生成延迟'
                        : '计费服务完成了月度账单生成';
                    break;
                case 'ocr':
                    content = level === 'critical'
                        ? 'OCR服务严重错误：图像识别服务不可用'
                        : 'OCR服务处理了100张图像';
                    break;
                case 'notification':
                    content = level === 'error'
                        ? '通知服务错误：邮件发送失败'
                        : '通知服务成功发送了25条短信';
                    break;
                case 'storage':
                    content = level === 'warning'
                        ? '存储服务警告：磁盘空间不足'
                        : '存储服务完成了日常备份';
                    break;
            }

            mockLogs.push({
                id: `SL-${200000 + i}`,
                timestamp,
                level,
                module,
                ipAddress,
                content
            });
        }
    }

    return { logs: mockLogs, total: filteredTotal };
}

// 格式化日期
function formatDate(date) {
    if (!date) return '-';
    if (typeof date === 'string') date = new Date(date);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 翻页
function changePage(page) {
    if (typeof page === 'number' && page > 0 && page <= totalPages.value) {
        currentPage.value = page;
        loadLogs();
    }
}

// 获取日志级别文本
function getLevelText(level) {
    switch (level) {
        case 'info': return '信息';
        case 'warning': return '警告';
        case 'error': return '错误';
        case 'critical': return '严重';
        default: return level;
    }
}

// 获取操作类型文本
function getActionTypeText(actionType) {
    switch (actionType) {
        case 'login': return '登录';
        case 'logout': return '登出';
        case 'create': return '创建';
        case 'update': return '更新';
        case 'delete': return '删除';
        case 'export': return '导出';
        case 'import': return '导入';
        default: return actionType;
    }
}

// 获取模块文本
function getModuleText(module) {
    switch (module) {
        case 'auth': return '认证服务';
        case 'billing': return '计费服务';
        case 'ocr': return 'OCR服务';
        case 'notification': return '通知服务';
        case 'storage': return '存储服务';
        default: return module;
    }
}

// 导出CSV
function exportToCSV() {
    // 模拟导出操作
    alert('正在导出日志数据，请稍后...');

    // 实际项目中，这里应该调用后端API进行导出
    setTimeout(() => {
        alert('导出成功!');
    }, 1500);
}
</script>

<style scoped>
.logs-page {
    position: relative;
    padding-bottom: 20px;
}

.page-title {
    margin-bottom: 20px;
}

.log-type-tabs {
    display: flex;
    margin-bottom: 20px;
    border-bottom: 1px solid #e8e8e8;
}

.tab-btn {
    padding: 10px 20px;
    border: none;
    background: none;
    font-size: 15px;
    cursor: pointer;
    position: relative;
    color: #595959;
}

.tab-btn.active {
    color: #1890ff;
    font-weight: 500;
}

.tab-btn.active:after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: #1890ff;
}

.filter-section {
    background-color: #f5f5f5;
    padding: 16px;
    border-radius: 4px;
    margin-bottom: 16px;
}

.filter-row {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 12px;
}

.filter-row:last-child {
    margin-bottom: 0;
}

.filter-item {
    display: flex;
    align-items: center;
}

.filter-select, .filter-input, .date-input {
    padding: 6px 10px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    margin: 0 8px 0 4px;
}

.filter-input, .date-input {
    width: 150px;
}

.to-text {
    margin: 0 4px;
}

.filter-actions {
    display: flex;
    gap: 8px;
    margin-left: auto;
}

.export-section {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
}

.btn {
    padding: 6px 15px;
    border-radius: 4px;
    border: none;
    cursor: pointer;
    font-size: 14px;
}

.btn-primary {
    background-color: #1890ff;
    color: white;
}

.btn-default {
    background-color: #f0f0f0;
    color: rgba(0, 0, 0, 0.65);
    border: 1px solid #d9d9d9;
}

.btn-export {
    background-color: #52c41a;
    color: white;
    display: flex;
    align-items: center;
    gap: 5px;
}

.export-icon {
    font-style: normal;
    font-weight: bold;
}

.log-table-wrapper {
    margin-bottom: 20px;
    overflow-x: auto;
}

.log-table {
    width: 100%;
    border-collapse: collapse;
    background-color: white;
}

.log-table th, .log-table td {
    border: 1px solid #e8e8e8;
    padding: 12px 8px;
    text-align: left;
}

.log-table th {
    background-color: #fafafa;
    font-weight: 500;
}

.content-column {
    width: 40%;
}

.log-content {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 300px;
}

.log-level {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
}

.level-info {
    background-color: #e6f7ff;
    color: #1890ff;
}

.level-warning {
    background-color: #fff7e6;
    color: #fa8c16;
}

.level-error {
    background-color: #fff1f0;
    color: #f5222d;
}

.level-critical {
    background-color: #f5222d;
    color: white;
}

.empty-data {
    text-align: center;
    padding: 24px;
    color: rgba(0, 0, 0, 0.45);
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
}

.page-btn {
    padding: 6px 12px;
    border: 1px solid #d9d9d9;
    background-color: white;
    cursor: pointer;
    border-radius: 4px;
}

.page-btn:disabled {
    color: rgba(0, 0, 0, 0.25);
    cursor: not-allowed;
}

.page-numbers {
    display: flex;
    gap: 8px;
}

.page-number {
    width: 32px;
    height: 32px;
    border: 1px solid #d9d9d9;
    background-color: white;
    border-radius: 4px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
}

.page-number.active {
    background-color: #1890ff;
    color: white;
    border-color: #1890ff;
}

.page-info {
    margin-left: 16px;
    color: rgba(0, 0, 0, 0.45);
}
</style>
