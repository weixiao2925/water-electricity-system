<template>
    <NuxtLayout>
        <div class="bills-container">
            <div class="page-header">
                <h1>账单中心</h1>
                <div class="action-bar">
                    <div class="filters">
                        <div class="filter-item">
                            <label>账单周期</label>
                            <select v-model="filters.period">
                                <option value="">全部</option>
                                <option value="202501">2025年1月</option>
                                <option value="202502">2025年2月</option>
                                <option value="202503">2025年3月</option>
                                <option value="202504">2025年4月</option>
                            </select>
                        </div>
                        <div class="filter-item">
                            <label>支付状态</label>
                            <select v-model="filters.status">
                                <option value="">全部</option>
                                <option value="paid">已支付</option>
                                <option value="unpaid">未支付</option>
                                <option value="overdue">已逾期</option>
                            </select>
                        </div>
                        <div class="filter-item">
                            <label>用户ID</label>
                            <input type="text" v-model="filters.userId" placeholder="请输入用户ID">
                        </div>
                        <button class="search-btn" @click="searchBills">搜索</button>
                        <button class="reset-btn" @click="resetFilters">重置</button>
                    </div>
                    <button class="export-btn" :disabled="selectedBills.length === 0" @click="exportBills">
                        导出选中账单 ({{ selectedBills.length }})
                    </button>
                </div>
            </div>

            <div class="bills-table">
                <table>
                    <thead>
                    <tr>
                        <th class="checkbox-cell">
                            <input type="checkbox" :checked="isAllSelected" @change="toggleSelectAll">
                        </th>
                        <th>账单编号</th>
                        <th>用户</th>
                        <th>账单周期</th>
                        <th>金额</th>
                        <th>创建日期</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="bill in bills" :key="bill.id" :class="{ 'selected-row': isSelected(bill.id) }">
                        <td class="checkbox-cell">
                            <input type="checkbox" :checked="isSelected(bill.id)" @change="toggleSelect(bill.id)">
                        </td>
                        <td>{{ bill.id }}</td>
                        <td>{{ bill.user }}</td>
                        <td>{{ formatPeriod(bill.period) }}</td>
                        <td>¥{{ bill.amount.toFixed(2) }}</td>
                        <td>{{ formatDate(bill.createdAt) }}</td>
                        <td>
              <span class="status-badge" :class="getStatusClass(bill.status)">
                {{ getStatusText(bill.status) }}
              </span>
                        </td>
                        <td class="action-cell">
                            <button class="view-btn" @click="viewBill(bill.id)">查看</button>
                            <button class="download-btn" @click="downloadBill(bill.id)">下载</button>
                        </td>
                    </tr>
                    <tr v-if="bills.length === 0">
                        <td colspan="8" class="empty-data">暂无数据</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination">
                <button
                    class="page-btn"
                    :disabled="currentPage === 1"
                    @click="changePage(currentPage - 1)"
                >
                    上一页
                </button>
                <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                <button
                    class="page-btn"
                    :disabled="currentPage === totalPages"
                    @click="changePage(currentPage + 1)"
                >
                    下一页
                </button>
                <span class="total-info">共 {{ totalRecords }} 条记录</span>
            </div>

            <!-- 导出进度弹窗 -->
            <div class="modal" v-if="showExportModal">
                <div class="modal-content">
                    <h3>正在准备导出文件</h3>
                    <div class="progress-bar">
                        <div class="progress" :style="{ width: `${exportProgress}%` }"></div>
                    </div>
                    <p>已完成: {{ exportProgress }}%</p>
                    <p v-if="exportProgress === 100">文件已准备完成，正在下载...</p>
                </div>
            </div>
        </div>
    </NuxtLayout>
</template>

<script setup>
definePageMeta({
    layout: 'admin'
});
// 模拟数据 - 实际项目中应从API获取
const mockBills = [
  { id: 'BILL-20250101', user: '张三', period: '202501', amount: 128.50, createdAt: '2025-02-01', status: 'paid' },
  { id: 'BILL-20250102', user: '李四', period: '202501', amount: 210.75, createdAt: '2025-02-01', status: 'paid' },
  { id: 'BILL-20250103', user: '王五', period: '202501', amount: 89.20, createdAt: '2025-02-01', status: 'unpaid' },
  { id: 'BILL-20250104', user: '赵六', period: '202501', amount: 156.30, createdAt: '2025-02-01', status: 'overdue' },
  { id: 'BILL-20250201', user: '张三', period: '202502', amount: 135.80, createdAt: '2025-03-01', status: 'paid' },
  { id: 'BILL-20250202', user: '李四', period: '202502', amount: 225.40, createdAt: '2025-03-01', status: 'unpaid' },
  { id: 'BILL-20250203', user: '王五', period: '202502', amount: 95.60, createdAt: '2025-03-01', status: 'paid' },
  { id: 'BILL-20250204', user: '赵六', period: '202502', amount: 168.20, createdAt: '2025-03-01', status: 'overdue' },
  { id: 'BILL-20250301', user: '张三', period: '202503', amount: 142.30, createdAt: '2025-04-01', status: 'paid' },
  { id: 'BILL-20250302', user: '李四', period: '202503', amount: 215.90, createdAt: '2025-04-01', status: 'unpaid' },
  { id: 'BILL-20250303', user: '王五', period: '202503', amount: 102.40, createdAt: '2025-04-01', status: 'paid' },
  { id: 'BILL-20250304', user: '赵六', period: '202503', amount: 178.60, createdAt: '2025-04-01', status: 'overdue' },
];

// 状态管理
const bills = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);
const totalPages = computed(() => Math.ceil(totalRecords.value / pageSize.value));
const selectedBills = ref([]);
const filters = ref({
  period: '',
  status: '',
  userId: ''
});
const showExportModal = ref(false);
const exportProgress = ref(0);

// 计算属性
const isAllSelected = computed(() => {
  return bills.value.length > 0 && selectedBills.value.length === bills.value.length;
});

// 初始化加载数据
onMounted(() => {
  fetchBills();
});

// 方法
function fetchBills() {
  // 模拟API请求
  const filteredBills = mockBills.filter(bill => {
    let match = true;
    if (filters.value.period && bill.period !== filters.value.period) match = false;
    if (filters.value.status && bill.status !== filters.value.status) match = false;
    if (filters.value.userId && !bill.user.includes(filters.value.userId)) match = false;
    return match;
  });

  totalRecords.value = filteredBills.length;

  // 分页
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  bills.value = filteredBills.slice(start, end);
}

function searchBills() {
  currentPage.value = 1;
  fetchBills();
}

function resetFilters() {
  filters.value = {
    period: '',
    status: '',
    userId: ''
  };
  searchBills();
}

function changePage(page) {
  currentPage.value = page;
  fetchBills();
}

function formatPeriod(period) {
  const year = period.substring(0, 4);
  const month = period.substring(4, 6);
  return `${year}年${month}月`;
}

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function getStatusText(status) {
  const statusMap = {
    'paid': '已支付',
    'unpaid': '未支付',
    'overdue': '已逾期'
  };
  return statusMap[status] || status;
}

function getStatusClass(status) {
  return `status-${status}`;
}

function isSelected(id) {
  return selectedBills.value.includes(id);
}

function toggleSelect(id) {
  const index = selectedBills.value.indexOf(id);
  if (index > -1) {
    selectedBills.value.splice(index, 1);
  } else {
    selectedBills.value.push(id);
  }
}

function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedBills.value = [];
  } else {
    selectedBills.value = bills.value.map(bill => bill.id);
  }
}

function viewBill(id) {
  // 实际项目中跳转到账单详情页或弹窗展示详情
  alert(`查看账单: ${id}`);
}

function downloadBill(id) {
  // 实际项目中调用下载API
  alert(`下载账单: ${id}`);
}

function exportBills() {
  if (selectedBills.value.length === 0) return;

  // 显示导出进度弹窗
  showExportModal.value = true;
  exportProgress.value = 0;

  // 模拟导出进度
  const timer = setInterval(() => {
    exportProgress.value += 10;
    if (exportProgress.value >= 100) {
      clearInterval(timer);
      setTimeout(() => {
        showExportModal.value = false;
        alert(`已导出 ${selectedBills.value.length} 个账单的ZIP文件`);
        // 实际项目中在这里触发真实的下载
      }, 1000);
    }
  }, 300);
}
</script>

<style scoped>
.bills-container {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 16px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filters {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-item label {
  font-size: 12px;
  color: #606266;
}

.filter-item select,
.filter-item input {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-width: 120px;
}

.search-btn,
.reset-btn,
.export-btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.search-btn {
  background-color: #1890ff;
  color: white;
}

.reset-btn {
  background-color: #f4f4f5;
  color: #606266;
  margin-left: 8px;
}

.export-btn {
  background-color: #67c23a;
  color: white;
}

.export-btn:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.bills-table {
  width: 100%;
  overflow-x: auto;
  margin-bottom: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

th, td {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

.checkbox-cell {
  width: 50px;
  text-align: center;
}

.selected-row {
  background-color: #f0f7ff;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-paid {
  background-color: #ecf5ff;
  color: #409eff;
}

.status-unpaid {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.status-overdue {
  background-color: #fef0f0;
  color: #f56c6c;
}

.action-cell {
  white-space: nowrap;
}

.view-btn,
.download-btn {
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background-color: white;
  cursor: pointer;
  font-size: 12px;
  margin-right: 8px;
}

.view-btn {
  color: #409eff;
}

.download-btn {
  color: #67c23a;
}

.empty-data {
  text-align: center;
  color: #909399;
  padding: 32px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #dcdfe6;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.page-info,
.total-info {
  color: #606266;
  font-size: 14px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 24px;
  border-radius: 4px;
  width: 400px;
  text-align: center;
}

.progress-bar {
  height: 10px;
  background-color: #ebeef5;
  border-radius: 5px;
  margin: 16px 0;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #1890ff;
  transition: width 0.3s ease;
}
</style>
