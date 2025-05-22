<template>
    <NuxtLayout>
        <div class="meters-management">
            <h1>表计管理</h1>

            <!-- 操作区域 -->
            <div class="action-bar">
                <div class="left-actions">
                    <button class="btn-primary" @click="showAddModal = true">新增表计</button>
                    <div class="csv-import">
                        <button class="btn-default" @click="$refs.fileInput.click()">CSV批量导入</button>
                        <input
                            ref="fileInput"
                            type="file"
                            accept=".csv"
                            style="display: none"
                            @change="handleFileUpload"
                        />
                    </div>
                    <button class="btn-danger" @click="batchDelete" :disabled="selectedMeters.length === 0">
                        批量删除
                    </button>
                </div>
                <div class="right-actions">
                    <div class="search-box">
                        <input
                            type="text"
                            v-model="searchQuery"
                            placeholder="搜索表计号/地址/所有人"
                            @input="handleSearch"
                        />
                        <button class="btn-search">搜索</button>
                    </div>
                </div>
            </div>

            <!-- 表格区域 -->
            <div class="table-container">
                <table class="meter-table">
                    <thead>
                    <tr>
                        <th width="50">
                            <input
                                type="checkbox"
                                :checked="isAllSelected"
                                @change="toggleSelectAll"
                            />
                        </th>
                        <th>表计ID</th>
                        <th>表计号</th>
                        <th>地址</th>
                        <th>所有人</th>
                        <th>安装日期</th>
                        <th>状态</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="meter in filteredMeters" :key="meter.id" :class="{ 'inactive': !meter.active }">
                        <td>
                            <input
                                type="checkbox"
                                :checked="selectedMeters.includes(meter.id)"
                                @change="toggleSelect(meter.id)"
                            />
                        </td>
                        <td>{{ meter.id }}</td>
                        <td>{{ meter.meterNumber }}</td>
                        <td>{{ meter.address }}</td>
                        <td>{{ meter.owner }}</td>
                        <td>{{ formatDate(meter.installDate) }}</td>
                        <td>
              <span :class="['status-badge', meter.active ? 'active' : 'inactive']">
                {{ meter.active ? '启用' : '停用' }}
              </span>
                        </td>
                        <td>{{ formatDate(meter.createdAt) }}</td>
                        <td class="actions">
                            <button class="btn-small" @click="editMeter(meter)">编辑</button>
                            <button
                                class="btn-small"
                                :class="meter.active ? 'btn-warning' : 'btn-success'"
                                @click="toggleMeterStatus(meter)"
                            >
                                {{ meter.active ? '停用' : '启用' }}
                            </button>
                            <button class="btn-small btn-danger" @click="deleteMeter(meter.id)">删除</button>
                        </td>
                    </tr>
                    <tr v-if="filteredMeters.length === 0">
                        <td colspan="9" class="no-data">暂无数据</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- 分页 -->
            <div class="pagination">
                <button
                    :disabled="currentPage === 1"
                    @click="changePage(currentPage - 1)"
                >
                    上一页
                </button>
                <span>{{ currentPage }} / {{ totalPages }}</span>
                <button
                    :disabled="currentPage === totalPages"
                    @click="changePage(currentPage + 1)"
                >
                    下一页
                </button>
            </div>

            <!-- 新增/编辑弹窗 -->
            <div class="modal" v-if="showAddModal || showEditModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>{{ showAddModal ? '新增表计' : '编辑表计' }}</h2>
                        <button class="close-btn" @click="closeModal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="meterNumber">表计号</label>
                            <input type="text" id="meterNumber" v-model="currentMeter.meterNumber" />
                        </div>
                        <div class="form-group">
                            <label for="address">地址</label>
                            <input type="text" id="address" v-model="currentMeter.address" />
                        </div>
                        <div class="form-group">
                            <label for="owner">所有人</label>
                            <input type="text" id="owner" v-model="currentMeter.owner" />
                        </div>
                        <div class="form-group">
                            <label for="installDate">安装日期</label>
                            <input type="date" id="installDate" v-model="currentMeter.installDate" />
                        </div>
                        <div class="form-group">
                            <label for="status">状态</label>
                            <select id="status" v-model="currentMeter.active">
                                <option :value="true">启用</option>
                                <option :value="false">停用</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn-cancel" @click="closeModal">取消</button>
                        <button class="btn-primary" @click="saveMeter">保存</button>
                    </div>
                </div>
            </div>

            <!-- CSV导入提示弹窗 -->
            <div class="modal" v-if="showImportModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>CSV批量导入</h2>
                        <button class="close-btn" @click="showImportModal = false">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div v-if="importStep === 'preview'">
                            <h3>导入预览</h3>
                            <p>共 {{ importData.length }} 条记录</p>
                            <div class="table-container">
                                <table class="meter-table">
                                    <thead>
                                    <tr>
                                        <th>表计号</th>
                                        <th>地址</th>
                                        <th>所有人</th>
                                        <th>安装日期</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr v-for="(item, index) in importData.slice(0, 5)" :key="index">
                                        <td>{{ item.meterNumber }}</td>
                                        <td>{{ item.address }}</td>
                                        <td>{{ item.owner }}</td>
                                        <td>{{ item.installDate }}</td>
                                    </tr>
                                    </tbody>
                                </table>
                                <p v-if="importData.length > 5" class="more-text">等更多数据...</p>
                            </div>
                        </div>
                        <div v-else-if="importStep === 'loading'">
                            <div class="loading-spinner"></div>
                            <p class="loading-text">正在导入，请稍候...</p>
                        </div>
                        <div v-else-if="importStep === 'result'">
                            <div class="import-result">
                                <div class="success-msg">
                                    成功导入 {{ importResult.success }} 条记录
                                </div>
                                <div class="error-msg" v-if="importResult.failed > 0">
                                    失败 {{ importResult.failed }} 条记录
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn-cancel" @click="showImportModal = false" v-if="importStep !== 'loading'">
                            关闭
                        </button>
                        <button
                            class="btn-primary"
                            @click="confirmImport"
                            v-if="importStep === 'preview'"
                        >
                            确认导入
                        </button>
                    </div>
                </div>
            </div>

            <!-- 确认删除弹窗 -->
            <div class="modal" v-if="showDeleteModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>确认删除</h2>
                        <button class="close-btn" @click="showDeleteModal = false">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p>确定要删除选中的 {{ deleteIds.length }} 个表计吗？此操作不可恢复。</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn-cancel" @click="showDeleteModal = false">取消</button>
                        <button class="btn-danger" @click="confirmDelete">确认删除</button>
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
// 数据状态
const meters = ref([]);
const selectedMeters = ref([]);
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = 10;
const totalItems = ref(0);

// 模态窗口状态
const showAddModal = ref(false);
const showEditModal = ref(false);
const showImportModal = ref(false);
const showDeleteModal = ref(false);
const currentMeter = ref({
  id: null,
  meterNumber: '',
  address: '',
  owner: '',
  installDate: '',
  active: true,
  createdAt: null
});
const deleteIds = ref([]);

// 导入状态
const importStep = ref('preview'); // preview, loading, result
const importData = ref([]);
const importResult = ref({
  success: 0,
  failed: 0
});

// 计算属性
const filteredMeters = computed(() => {
  if (!searchQuery.value) {
    return meters.value;
  }

  const query = searchQuery.value.toLowerCase();
  return meters.value.filter(meter =>
    meter.meterNumber.toLowerCase().includes(query) ||
    meter.address.toLowerCase().includes(query) ||
    meter.owner.toLowerCase().includes(query)
  );
});

const totalPages = computed(() => Math.ceil(totalItems.value / pageSize));

const isAllSelected = computed(() => {
  return meters.value.length > 0 && selectedMeters.value.length === meters.value.length;
});

// 生命周期钩子
onMounted(() => {
  fetchMeters();
});

// 方法
function fetchMeters() {
  // 模拟API调用
  setTimeout(() => {
    // 模拟数据
    meters.value = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      meterNumber: `M-${100000 + i}`,
      address: `测试地址 ${i + 1}`,
      owner: `用户 ${i + 1}`,
      installDate: new Date(2025, i % 12, (i % 28) + 1).toISOString().split('T')[0],
      active: i % 5 !== 0, // 每5个有一个停用的
      createdAt: new Date(2025, 0, 1).toISOString()
    }));
    totalItems.value = meters.value.length;
  }, 300);
}

function handleSearch() {
  currentPage.value = 1;
}

function changePage(page) {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
}

function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedMeters.value = [];
  } else {
    selectedMeters.value = meters.value.map(meter => meter.id);
  }
}

function toggleSelect(id) {
  const index = selectedMeters.value.indexOf(id);
  if (index === -1) {
    selectedMeters.value.push(id);
  } else {
    selectedMeters.value.splice(index, 1);
  }
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function editMeter(meter) {
  currentMeter.value = { ...meter };
  showEditModal.value = true;
}

function toggleMeterStatus(meter) {
  const updatedMeter = { ...meter, active: !meter.active };
  // 实际应用中这里会有API调用
  const index = meters.value.findIndex(m => m.id === meter.id);
  if (index !== -1) {
    meters.value[index] = updatedMeter;
  }
}

function deleteMeter(id) {
  deleteIds.value = [id];
  showDeleteModal.value = true;
}

function batchDelete() {
  if (selectedMeters.value.length === 0) return;
  deleteIds.value = [...selectedMeters.value];
  showDeleteModal.value = true;
}

function confirmDelete() {
  // 实际应用中这里会有API调用
  meters.value = meters.value.filter(meter => !deleteIds.value.includes(meter.id));
  selectedMeters.value = selectedMeters.value.filter(id => !deleteIds.value.includes(id));
  showDeleteModal.value = false;
  deleteIds.value = [];
}

function closeModal() {
  showAddModal.value = false;
  showEditModal.value = false;
  currentMeter.value = {
    id: null,
    meterNumber: '',
    address: '',
    owner: '',
    installDate: '',
    active: true,
    createdAt: null
  };
}

function saveMeter() {
  if (showAddModal.value) {
    // 新增逻辑
    const newMeter = {
      ...currentMeter.value,
      id: meters.value.length + 1,
      createdAt: new Date().toISOString()
    };
    meters.value.unshift(newMeter);
  } else {
    // 编辑逻辑
    const index = meters.value.findIndex(m => m.id === currentMeter.value.id);
    if (index !== -1) {
      meters.value[index] = { ...currentMeter.value };
    }
  }
  closeModal();
}

function handleFileUpload(event) {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    const csv = e.target.result;
    const lines = csv.split('\n');
    const headers = lines[0].split(',');

    // 简单的CSV解析
    const result = [];
    for (let i = 1; i < lines.length; i++) {
      if (!lines[i].trim()) continue;

      const obj = {};
      const currentLine = lines[i].split(',');

      for (let j = 0; j < headers.length; j++) {
        obj[headers[j].trim()] = currentLine[j].trim();
      }

      result.push({
        meterNumber: obj.meterNumber || `未命名-${i}`,
        address: obj.address || '未知地址',
        owner: obj.owner || '未知',
        installDate: obj.installDate || new Date().toISOString().split('T')[0],
        active: true
      });
    }

    importData.value = result;
    importStep.value = 'preview';
    showImportModal.value = true;
  };

  reader.readAsText(file);
  // 重置文件输入，以便于可以再次选择同一文件
  event.target.value = '';
}

function confirmImport() {
  importStep.value = 'loading';

  // 模拟导入过程
  setTimeout(() => {
    // 新建所有表计
    const newMeters = importData.value.map((item, index) => ({
      id: meters.value.length + index + 1,
      ...item,
      createdAt: new Date().toISOString()
    }));

    // 模拟一些导入失败的情况
    const successCount = importData.value.length - Math.floor(Math.random() * 3);

    // 添加到表计列表
    meters.value = [...newMeters.slice(0, successCount), ...meters.value];

    // 更新导入结果
    importResult.value = {
      success: successCount,
      failed: importData.value.length - successCount
    };

    importStep.value = 'result';
  }, 1500);
}
</script>

<style scoped>
.meters-management {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  font-size: 24px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.left-actions, .right-actions {
  display: flex;
  gap: 12px;
}

button {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  border: none;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
}

.btn-default {
  background-color: #f0f0f0;
  border: 1px solid #d9d9d9;
}

.btn-danger {
  background-color: #ff4d4f;
  color: white;
}

.btn-warning {
  background-color: #faad14;
  color: white;
}

.btn-success {
  background-color: #52c41a;
  color: white;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
}

.search-box {
  display: flex;
}

.search-box input {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px 0 0 4px;
  width: 250px;
}

.btn-search {
  border-radius: 0 4px 4px 0;
  background-color: #1890ff;
  color: white;
}

.table-container {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.meter-table {
  width: 100%;
  border-collapse: collapse;
}

.meter-table th, .meter-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.meter-table th {
  background-color: #fafafa;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.status-badge.active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.inactive {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.pagination button {
  padding: 6px 12px;
  background-color: #f0f0f0;
  border: 1px solid #d9d9d9;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  border-radius: 4px;
  width: 500px;
  max-width: 90%;
}

.modal-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.modal-body {
  padding: 16px;
  max-height: 70vh;
  overflow-y: auto;
}

.modal-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.btn-cancel {
  background-color: #f0f0f0;
  border: 1px solid #d9d9d9;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;
  margin: 0 auto;
}

.loading-text {
  text-align: center;
  margin-top: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.import-result {
  text-align: center;
}

.success-msg {
  color: #52c41a;
  font-size: 16px;
  margin-bottom: 8px;
}

.error-msg {
  color: #ff4d4f;
  font-size: 16px;
}

.more-text {
  text-align: center;
  color: #999;
  padding: 8px;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 24px;
}

tr.inactive {
  background-color: #fafafa;
  color: #999;
}
</style>
