<template>
    <NuxtLayout>
        <div class="ocr-tasks-page">
            <h1 class="page-title">OCR任务监控</h1>

            <!-- 筛选和操作区 -->
            <div class="action-bar">
                <div class="filters">
                    <div class="filter-item">
                        <span>任务状态：</span>
                        <select v-model="filters.status" class="filter-select" @change="loadTasks">
                            <option value="">全部</option>
                            <option value="pending">等待处理</option>
                            <option value="processing">处理中</option>
                            <option value="success">成功</option>
                            <option value="failed">失败</option>
                        </select>
                    </div>
                    <div class="filter-item">
                        <span>时间范围：</span>
                        <input type="date" v-model="filters.startDate" class="date-input" @change="loadTasks">
                        <span>至</span>
                        <input type="date" v-model="filters.endDate" class="date-input" @change="loadTasks">
                    </div>
                    <button class="btn btn-primary" @click="loadTasks">查询</button>
                    <button class="btn btn-default" @click="resetFilters">重置</button>
                </div>
                <div class="operations">
                    <button class="btn btn-danger" :disabled="!selectedTasks.length" @click="confirmBatchDelete">
                        批量删除
                    </button>
                    <button class="btn btn-warning" :disabled="!hasFailedTasksSelected" @click="batchRetry">
                        失败重试
                    </button>
                </div>
            </div>

            <!-- 任务列表表格 -->
            <div class="task-table-wrapper">
                <table class="task-table">
                    <thead>
                    <tr>
                        <th width="50">
                            <input type="checkbox" v-model="selectAll" @change="toggleSelectAll">
                        </th>
                        <th>任务ID</th>
                        <th>表计ID</th>
                        <th>创建时间</th>
                        <th>处理时间</th>
                        <th>状态</th>
                        <th>结果</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="task in tasks" :key="task.id" :class="{'failed-row': task.status === 'failed'}">
                        <td>
                            <input type="checkbox" v-model="selectedTasks" :value="task.id">
                        </td>
                        <td>{{ task.id }}</td>
                        <td>{{ task.meterId }}</td>
                        <td>{{ formatDate(task.createdAt) }}</td>
                        <td>{{ formatDate(task.processedAt) }}</td>
                        <td>
              <span class="status-badge" :class="getStatusClass(task.status)">
                {{ getStatusText(task.status) }}
              </span>
                        </td>
                        <td>
                            <span v-if="task.status === 'success'">{{ task.result }}</span>
                            <span v-else-if="task.status === 'failed'" class="error-msg">{{ task.errorMessage }}</span>
                            <span v-else>-</span>
                        </td>
                        <td>
                            <button v-if="task.status === 'failed'" class="btn-mini btn-warning" @click="retryTask(task.id)">
                                重试
                            </button>
                            <button class="btn-mini btn-danger" @click="confirmDeleteTask(task.id)">
                                删除
                            </button>
                            <button class="btn-mini btn-info" @click="viewTaskDetail(task.id)">
                                详情
                            </button>
                        </td>
                    </tr>
                    <tr v-if="tasks.length === 0">
                        <td colspan="8" class="empty-data">暂无数据</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- 分页控件 -->
            <div class="pagination">
                <button :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">上一页</button>
                <span>第 {{ currentPage }}/{{ totalPages }} 页</span>
                <button :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">下一页</button>
                <span class="page-info">共 {{ totalItems }} 条</span>
            </div>

            <!-- 确认框 -->
            <div v-if="showDeleteConfirm" class="confirm-modal">
                <div class="confirm-content">
                    <h3>确认删除</h3>
                    <p>{{ deleteConfirmMessage }}</p>
                    <div class="confirm-buttons">
                        <button class="btn btn-default" @click="cancelDelete">取消</button>
                        <button class="btn btn-danger" @click="confirmDelete">确认删除</button>
                    </div>
                </div>
            </div>

            <!-- 任务详情弹窗 -->
            <div v-if="showTaskDetail" class="task-detail-modal">
                <div class="task-detail-content">
                    <div class="modal-header">
                        <h3>任务详情</h3>
                        <button class="close-btn" @click="showTaskDetail = false">×</button>
                    </div>
                    <div class="modal-body" v-if="currentTask">
                        <div class="detail-row">
                            <span class="detail-label">任务ID:</span>
                            <span>{{ currentTask.id }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">表计ID:</span>
                            <span>{{ currentTask.meterId }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">创建时间:</span>
                            <span>{{ formatDate(currentTask.createdAt) }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">处理时间:</span>
                            <span>{{ formatDate(currentTask.processedAt) }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">状态:</span>
                            <span class="status-badge" :class="getStatusClass(currentTask.status)">
              {{ getStatusText(currentTask.status) }}
            </span>
                        </div>
                        <div class="detail-row" v-if="currentTask.status === 'success'">
                            <span class="detail-label">识别结果:</span>
                            <span>{{ currentTask.result }}</span>
                        </div>
                        <div class="detail-row" v-if="currentTask.status === 'failed'">
                            <span class="detail-label">错误信息:</span>
                            <span class="error-msg">{{ currentTask.errorMessage }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">图片:</span>
                            <div class="task-image">
                                <img :src="currentTask.imageUrl" alt="表计图片">
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="showTaskDetail = false">关闭</button>
                    </div>
                </div>
            </div>
        </div>
    </NuxtLayout>

</template>

<script setup>

definePageMeta({
    layout: 'admin'
});
// 过滤条件
const filters = ref({
  status: '',
  startDate: '',
  endDate: ''
});

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

// 任务数据
const tasks = ref([]);
const selectedTasks = ref([]);
const selectAll = ref(false);

// 弹窗控制
const showDeleteConfirm = ref(false);
const showTaskDetail = ref(false);
const currentTask = ref(null);
const taskToDelete = ref(null);
const isBatchDelete = ref(false);

// 是否有失败的任务被选中
const hasFailedTasksSelected = computed(() => {
  return selectedTasks.value.some(id =>
    tasks.value.find(task => task.id === id && task.status === 'failed')
  );
});

// 删除确认信息
const deleteConfirmMessage = computed(() => {
  if (isBatchDelete.value) {
    return `确定要删除选中的 ${selectedTasks.value.length} 个任务吗？此操作不可恢复。`;
  }
  return '确定要删除此任务吗？此操作不可恢复。';
});

// 初始化加载数据
onMounted(() => {
  loadTasks();
});

// 加载任务数据
function loadTasks() {
  // 模拟API调用，实际项目中应替换为真实API请求
  setTimeout(() => {
    const mockData = generateMockData();
    tasks.value = mockData.tasks;
    totalItems.value = mockData.total;
    selectedTasks.value = [];
    selectAll.value = false;
  }, 300);
}

// 生成模拟数据
function generateMockData() {
  const statuses = ['pending', 'processing', 'success', 'failed'];
  const total = 87;

  // 应用过滤器
  let filteredTotal = total;
  if (filters.value.status) {
    filteredTotal = Math.floor(total * 0.3);
  }

  const mockTasks = [];
  const start = (currentPage.value - 1) * pageSize.value;
  const end = Math.min(start + pageSize.value, filteredTotal);

  for (let i = start; i < end; i++) {
    const taskStatus = filters.value.status || statuses[Math.floor(Math.random() * statuses.length)];
    const createdAt = new Date(Date.now() - Math.random() * 30 * 86400000);

    mockTasks.push({
      id: `TASK-${1000 + i}`,
      meterId: `METER-${2000 + Math.floor(Math.random() * 100)}`,
      createdAt: createdAt,
      processedAt: taskStatus !== 'pending' ? new Date(createdAt.getTime() + Math.random() * 3600000) : null,
      status: taskStatus,
      result: taskStatus === 'success' ? `${Math.floor(Math.random() * 1000)}.${Math.floor(Math.random() * 10)}` : null,
      errorMessage: taskStatus === 'failed' ? '图像质量过低，无法识别数字' : null,
      imageUrl: 'https://via.placeholder.com/300x200'
    });
  }

  return { tasks: mockTasks, total: filteredTotal };
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

// 获取状态文本
function getStatusText(status) {
  switch (status) {
    case 'pending': return '等待处理';
    case 'processing': return '处理中';
    case 'success': return '成功';
    case 'failed': return '失败';
    default: return status;
  }
}

// 获取状态CSS类
function getStatusClass(status) {
  switch (status) {
    case 'pending': return 'status-pending';
    case 'processing': return 'status-processing';
    case 'success': return 'status-success';
    case 'failed': return 'status-failed';
    default: return '';
  }
}

// 全选/取消全选
function toggleSelectAll() {
  if (selectAll.value) {
    selectedTasks.value = tasks.value.map(task => task.id);
  } else {
    selectedTasks.value = [];
  }
}

// 重置筛选条件
function resetFilters() {
  filters.value = {
    status: '',
    startDate: '',
    endDate: ''
  };
  loadTasks();
}

// 翻页
function changePage(page) {
  currentPage.value = page;
  loadTasks();
}

// 查看任务详情
function viewTaskDetail(taskId) {
  currentTask.value = tasks.value.find(task => task.id === taskId);
  showTaskDetail.value = true;
}

// 重试任务
function retryTask(taskId) {
  // 模拟重试API调用
  console.log(`重试任务: ${taskId}`);
  alert(`已提交重试请求: ${taskId}`);

  // 模拟状态更新
  const taskIndex = tasks.value.findIndex(task => task.id === taskId);
  if (taskIndex !== -1) {
    tasks.value[taskIndex].status = 'processing';
  }
}

// 批量重试
function batchRetry() {
  const failedTaskIds = selectedTasks.value.filter(id =>
    tasks.value.find(task => task.id === id && task.status === 'failed')
  );

  if (failedTaskIds.length === 0) {
    return alert('没有可重试的失败任务');
  }

  // 模拟重试API调用
  console.log(`批量重试任务: ${failedTaskIds.join(', ')}`);
  alert(`已提交${failedTaskIds.length}个任务的重试请求`);

  // 模拟状态更新
  failedTaskIds.forEach(id => {
    const taskIndex = tasks.value.findIndex(task => task.id === id);
    if (taskIndex !== -1) {
      tasks.value[taskIndex].status = 'processing';
    }
  });

  // 更新选中状态
  selectedTasks.value = selectedTasks.value.filter(id => !failedTaskIds.includes(id));
}

// 确认删除单个任务
function confirmDeleteTask(taskId) {
  taskToDelete.value = taskId;
  isBatchDelete.value = false;
  showDeleteConfirm.value = true;
}

// 确认批量删除
function confirmBatchDelete() {
  if (selectedTasks.value.length === 0) return;

  isBatchDelete.value = true;
  showDeleteConfirm.value = true;
}

// 取消删除
function cancelDelete() {
  showDeleteConfirm.value = false;
  taskToDelete.value = null;
  isBatchDelete.value = false;
}

// 确认删除操作
function confirmDelete() {
  if (isBatchDelete.value) {
    // 模拟批量删除API调用
    console.log(`批量删除任务: ${selectedTasks.value.join(', ')}`);

    // 移除已删除的任务
    tasks.value = tasks.value.filter(task => !selectedTasks.value.includes(task.id));
    selectedTasks.value = [];
  } else {
    // 模拟单个删除API调用
    console.log(`删除任务: ${taskToDelete.value}`);

    // 移除已删除的任务
    tasks.value = tasks.value.filter(task => task.id !== taskToDelete.value);
    selectedTasks.value = selectedTasks.value.filter(id => id !== taskToDelete.value);
  }

  showDeleteConfirm.value = false;
  taskToDelete.value = null;
  isBatchDelete.value = false;
}
</script>

<style scoped>
.ocr-tasks-page {
  position: relative;
}

.page-title {
  margin-bottom: 24px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-select, .date-input {
  padding: 6px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  margin: 0 8px;
}

.operations {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 15px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
}

.btn-danger {
  background-color: #f5222d;
  color: white;
}

.btn-warning {
  background-color: #faad14;
  color: white;
}

.btn-default {
  background-color: #f0f0f0;
  color: rgba(0, 0, 0, 0.65);
  border: 1px solid #d9d9d9;
}

.btn-info {
  background-color: #1890ff;
  color: white;
}

.btn-mini {
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 2px;
  margin-right: 5px;
  border: none;
  cursor: pointer;
}

.task-table-wrapper {
  margin-bottom: 16px;
  overflow-x: auto;
}

.task-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

.task-table th, .task-table td {
  border: 1px solid #e8e8e8;
  padding: 12px 8px;
  text-align: left;
}

.task-table th {
  background-color: #fafafa;
  font-weight: 500;
}

.failed-row {
  background-color: #fff1f0;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.status-pending {
  background-color: #d9d9d9;
  color: rgba(0, 0, 0, 0.65);
}

.status-processing {
  background-color: #1890ff;
  color: white;
}

.status-success {
  background-color: #52c41a;
  color: white;
}

.status-failed {
  background-color: #f5222d;
  color: white;
}

.error-msg {
  color: #f5222d;
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
  gap: 16px;
}

.pagination button {
  padding: 4px 12px;
  border: 1px solid #d9d9d9;
  background-color: white;
  cursor: pointer;
  border-radius: 4px;
}

.pagination button:disabled {
  color: rgba(0, 0, 0, 0.25);
  cursor: not-allowed;
}

.page-info {
  margin-left: 16px;
  color: rgba(0, 0, 0, 0.45);
}

.confirm-modal, .task-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.confirm-content, .task-detail-content {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 24px;
  min-width: 400px;
}

.task-detail-content {
  width: 600px;
  max-width: 90vw;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.modal-footer {
  margin-top: 24px;
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.45);
}

.detail-row {
  margin-bottom: 16px;
}

.detail-label {
  display: inline-block;
  width: 80px;
  color: rgba(0, 0, 0, 0.65);
}

.task-image {
  margin-top: 8px;
}

.task-image img {
  max-width: 100%;
  border: 1px solid #f0f0f0;
}

.confirm-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
