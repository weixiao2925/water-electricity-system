<template>
    <NuxtLayout name="admin">
        <div class="overview-page">
            <h1 class="page-title">系统概览</h1>

            <!-- 统计卡片区域 -->
            <div class="stat-cards">
                <div class="stat-card" v-for="(card, index) in statCards" :key="index">
                    <div class="card-title">{{ card.title }}</div>
                    <div class="card-value">{{ card.value }}</div>
                    <div class="card-footer">
          <span :class="['trend', card.trend >= 0 ? 'up' : 'down']">
            {{ Math.abs(card.trend) }}% {{ card.trend >= 0 ? '↑' : '↓' }}
          </span>
                        <span class="period">较上月</span>
                    </div>
                </div>
            </div>

            <!-- 年度折线图 -->
            <div class="chart-section">
                <div class="section-header">
                    <h2>年度用量/费用趋势</h2>
                    <div class="chart-controls">
                        <select v-model="chartType">
                            <option value="usage">用量</option>
                            <option value="cost">费用</option>
                        </select>
                        <select v-model="chartYear">
                            <option v-for="year in availableYears" :key="year" :value="year">{{ year }}年</option>
                        </select>
                    </div>
                </div>
                <ClientOnly>
                    <line-chart :chart-data="chartData" :chart-options="chartOptions" />
                </ClientOnly>
            </div>

            <!-- 失败任务列表 -->
            <div class="tasks-section">
                <div class="section-header">
                    <h2>OCR失败任务</h2>
                    <button class="refresh-btn" @click="refreshTasks">刷新</button>
                </div>
                <div class="tasks-table">
                    <table>
                        <thead>
                        <tr>
                            <th>任务ID</th>
                            <th>表计名称</th>
                            <th>失败原因</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="task in failedTasks" :key="task.id">
                            <td>{{ task.id }}</td>
                            <td>{{ task.meterName }}</td>
                            <td>{{ task.errorMessage }}</td>
                            <td>{{ formatDate(task.createdAt) }}</td>
                            <td>
                                <button class="retry-btn" @click="retryTask(task.id)">重试</button>
                                <button class="details-btn" @click="viewTaskDetails(task.id)">详情</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="pagination">
                        <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
                        <span>{{ currentPage }} / {{ totalPages }}</span>
                        <button :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
                    </div>
                </div>
            </div>
        </div>
    </NuxtLayout>
</template>

<script setup>
// 使用管理布局
definePageMeta({
  layout: 'admin'
});

import { ref, computed, onMounted } from 'vue';

// 统计卡片数据
const statCards = ref([
  { title: '本月OCR处理量', value: '3,256', trend: 12.5 },
  { title: '本月成功率', value: '98.7%', trend: 2.1 },
  { title: '本月处理费用', value: '¥5,643', trend: -3.2 },
  { title: '存储使用量', value: '254GB', trend: 8.7 }
]);

// 图表相关
const chartType = ref('usage');
const chartYear = ref(new Date().getFullYear());
const availableYears = [2022, 2023, 2024];

// 模拟获取图表数据
const getChartData = () => {
  // 实际项目中应调用API获取数据
  const monthlyData = Array.from({ length: 12 }, () => Math.floor(Math.random() * 1000));
  return {
    labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    datasets: [
      {
        label: chartType.value === 'usage' ? '月度处理量' : '月度费用',
        data: monthlyData,
        borderColor: '#1890ff',
        backgroundColor: 'rgba(24, 144, 255, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  };
};

const chartData = computed(() => getChartData());
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
    tooltip: {
      mode: 'index',
      intersect: false,
    }
  },
  scales: {
    y: {
      beginAtZero: true
    }
  }
};

// 失败任务列表相关
const failedTasks = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = 10;

// 获取失败任务列表
const fetchFailedTasks = async () => {
  try {
    // 实际项目中应调用API获取数据
    // const { data } = await api.getFailedTasks(currentPage.value, pageSize);

    // 模拟数据
    failedTasks.value = Array.from({ length: 10 }, (_, i) => ({
      id: `TASK-${1000 + i}`,
      meterName: `电表-${200 + i}`,
      errorMessage: i % 3 === 0 ? '图像质量不足' : i % 3 === 1 ? '数字识别失败' : '请求超时',
      createdAt: new Date(Date.now() - Math.random() * 86400000 * 7)
    }));

    totalPages.value = 5; // 模拟总页数
  } catch (error) {
    console.error('获取失败任务列表失败:', error);
  }
};

// 重试任务
const retryTask = async (taskId) => {
  try {
    console.log(`重试任务: ${taskId}`);
    // 实际项目中应调用API重试任务
    // await api.retryTask(taskId);
    await fetchFailedTasks(); // 刷新列表
  } catch (error) {
    console.error('重试任务失败:', error);
  }
};

// 查看任务详情
const viewTaskDetails = (taskId) => {
  console.log(`查看任务详情: ${taskId}`);
  // 实际项目中应实现查看详情的逻辑，例如弹窗或导航到详情页
};

// 刷新任务列表
const refreshTasks = () => {
  fetchFailedTasks();
};

// 日期格式化
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 监听分页变化
watch(currentPage, () => {
  fetchFailedTasks();
});

// 组件挂载时获取数据
onMounted(() => {
  fetchFailedTasks();
});
</script>

<style scoped>
.overview-page {
  max-width: 100%;
}

.page-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 500;
}

/* 统计卡片样式 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.card-title {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.trend {
  margin-right: 4px;
}

.trend.up {
  color: #52c41a;
}

.trend.down {
  color: #f5222d;
}

.period {
  color: #8c8c8c;
}

/* 图表区域样式 */
.chart-section {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

.chart-controls {
  display: flex;
  gap: 12px;
}

.chart-controls select {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  background-color: white;
}

/* 线性图表容器，确保图表有足够高度 */
.line-chart {
  height: 300px;
  width: 100%;
}

/* 任务列表区域样式 */
.tasks-section {
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.refresh-btn {
  padding: 6px 12px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 2px;
  cursor: pointer;
}

.tasks-table {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  text-align: left;
  padding: 12px 16px;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background-color: #fafafa;
  font-weight: 500;
}

.retry-btn, .details-btn {
  padding: 4px 8px;
  margin-right: 8px;
  border: none;
  border-radius: 2px;
  cursor: pointer;
}

.retry-btn {
  background-color: #52c41a;
  color: white;
}

.details-btn {
  background-color: #f0f0f0;
  color: #595959;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  gap: 12px;
}

.pagination button {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  cursor: pointer;
}

.pagination button:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
