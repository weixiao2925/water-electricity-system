<script setup lang="ts">
const route = useRoute();
const router = useRouter();

// 获取表计ID
const meterId = computed(() => Number(route.params.id));
console.log(route.params.id)
onMounted(() => {
    console.log(route.params.id)
})


// 模拟表计详细数据
const metersData = {
  1: {
    id: 1,
    type: '水表',
    location: '厨房',
    model: 'WM-100A',
    serialNumber: 'WM20220115001',
    manufacturer: '水表制造商A',
    installDate: '2022-01-15',
    lastMaintenance: '2023-01-15',
    lastReading: 123.5,
    unit: 'm³',
    status: '正常',
    readings: [
      { date: '2023-01-01', value: 85.3, cost: 52.1 },
      { date: '2023-02-01', value: 93.7, cost: 58.6 },
      { date: '2023-03-01', value: 101.2, cost: 62.4 },
      { date: '2023-04-01', value: 109.8, cost: 68.5 },
      { date: '2023-05-01', value: 118.3, cost: 73.2 },
      { date: '2023-06-01', value: 123.5, cost: 78.4 }
    ]
  },
  2: {
    id: 2,
    type: '电表',
    location: '配电室',
    model: 'EM-200B',
    serialNumber: 'EM20211120002',
    manufacturer: '电表制造商B',
    installDate: '2021-11-20',
    lastMaintenance: '2022-11-20',
    lastReading: 568.7,
    unit: 'kWh',
    status: '正常',
    readings: [
      { date: '2023-01-01', value: 498.1, cost: 152.3 },
      { date: '2023-02-01', value: 510.7, cost: 158.2 },
      { date: '2023-03-01', value: 525.4, cost: 165.8 },
      { date: '2023-04-01', value: 540.2, cost: 173.5 },
      { date: '2023-05-01', value: 552.9, cost: 190.1 },
      { date: '2023-06-01', value: 568.7, cost: 195.6 }
    ]
  },
  3: {
    id: 3,
    type: '气表',
    location: '厨房',
    model: 'GM-150C',
    serialNumber: 'GM20220305003',
    manufacturer: '气表制造商C',
    installDate: '2022-03-05',
    lastMaintenance: '2023-03-05',
    lastReading: 89.2,
    unit: 'm³',
    status: '正常',
    readings: [
      { date: '2023-01-01', value: 78.1, cost: 145.2 },
      { date: '2023-02-01', value: 80.5, cost: 149.7 },
      { date: '2023-03-01', value: 83.2, cost: 154.8 },
      { date: '2023-04-01', value: 85.6, cost: 159.2 },
      { date: '2023-05-01', value: 87.4, cost: 162.6 },
      { date: '2023-06-01', value: 89.2, cost: 167.3 }
    ]
  },
  4: {
    id: 4,
    type: '水表',
    location: '卫生间',
    model: 'WM-100A',
    serialNumber: 'WM20220210004',
    manufacturer: '水表制造商A',
    installDate: '2022-02-10',
    lastMaintenance: '2023-02-10',
    lastReading: 78.3,
    unit: 'm³',
    status: '需要检查',
    readings: [
      { date: '2023-01-01', value: 55.2, cost: 34.1 },
      { date: '2023-02-01', value: 60.7, cost: 38.2 },
      { date: '2023-03-01', value: 67.5, cost: 42.5 },
      { date: '2023-04-01', value: 72.8, cost: 45.9 },
      { date: '2023-05-01', value: 76.1, cost: 48.0 },
      { date: '2023-06-01', value: 78.3, cost: 49.4 }
    ]
  }
};

// 获取当前表计数据
const meter = computed(() => {
  return metersData[meterId.value as keyof typeof metersData] || null;
});

// 图表数据
const chartData = computed(() => {
  if (!meter.value) return { dates: [], values: [], costs: [] };

  return {
    dates: meter.value.readings.map(r => r.date),
    values: meter.value.readings.map(r => r.value),
    costs: meter.value.readings.map(r => r.cost)
  };
});

// 当前选中的图表类型
const selectedChartType = ref('读数');

// 返回到表计列表页
const goBack = () => {
  router.push('/home/meters');
};

definePageMeta({
    layout : "home"
})
</script>

<template>
    <NuxtLayout>
        <div class="meter-detail-page">
            <div class="back-button" @click="goBack">
                <span>← 返回表计列表</span>
            </div>

            <div v-if="meter" class="meter-content">
                <div class="meter-header" :class="meter.type.toLowerCase()">
                    <h1>{{ meter.type }} - {{ meter.location }}</h1>
                    <div class="meter-status" :class="meter.status === '正常' ? 'normal' : meter.status === '需要检查' ? 'warning' : 'error'">
                        {{ meter.status }}
                    </div>
                </div>

                <!-- 表计基本信息 -->
                <div class="info-card">
                    <h2>基本信息</h2>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="label">表计型号</div>
                            <div class="value">{{ meter.model }}</div>
                        </div>
                        <div class="info-item">
                            <div class="label">序列号</div>
                            <div class="value">{{ meter.serialNumber }}</div>
                        </div>
                        <div class="info-item">
                            <div class="label">制造商</div>
                            <div class="value">{{ meter.manufacturer }}</div>
                        </div>
                        <div class="info-item">
                            <div class="label">安装日期</div>
                            <div class="value">{{ meter.installDate }}</div>
                        </div>
                        <div class="info-item">
                            <div class="label">最近检修</div>
                            <div class="value">{{ meter.lastMaintenance }}</div>
                        </div>
                        <div class="info-item">
                            <div class="label">当前读数</div>
                            <div class="value">{{ meter.lastReading }} {{ meter.unit }}</div>
                        </div>
                    </div>
                </div>

                <!-- 图表区域 -->
                <div class="chart-card">
                    <div class="chart-header">
                        <h2>历史数据</h2>
                        <div class="chart-tabs">
                            <div
                                class="chart-tab"
                                :class="{ active: selectedChartType === '读数' }"
                                @click="selectedChartType = '读数'"
                            >
                                读数变化
                            </div>
                            <div
                                class="chart-tab"
                                :class="{ active: selectedChartType === '费用' }"
                                @click="selectedChartType = '费用'"
                            >
                                费用变化
                            </div>
                        </div>
                    </div>

                    <!-- 模拟折线图 -->
                    <div class="chart-container">
                        <div class="chart-line">
                            <div
                                v-for="(date, index) in chartData.dates"
                                :key="date"
                                class="chart-point"
                                :style="{
                height: selectedChartType === '读数'
                  ? `${(chartData.values[index] / Math.max(...chartData.values)) * 80}%`
                  : `${(chartData.costs[index] / Math.max(...chartData.costs)) * 80}%`
              }"
                            >
                                <div class="point-value">
                                    {{ selectedChartType === '读数' ? chartData.values[index] : '¥' + chartData.costs[index].toFixed(2) }}
                                </div>
                                <div class="point"></div>
                                <div class="date-label">{{ date }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 历史读数列表 -->
                <div class="readings-card">
                    <h2>历史读数记录</h2>
                    <table>
                        <thead>
                        <tr>
                            <th>日期</th>
                            <th>读数 ({{ meter.unit }})</th>
                            <th>费用 (¥)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="reading in meter.readings" :key="reading.date">
                            <td>{{ reading.date }}</td>
                            <td>{{ reading.value }}</td>
                            <td>{{ reading.cost.toFixed(2) }}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 错误状态 -->
            <div v-else class="error-state">
                <div class="error-message">
                    未找到该表计信息
                </div>
                <button class="back-btn" @click="goBack">返回表计列表</button>
            </div>
        </div>
    </NuxtLayout>

</template>

<style scoped>
.meter-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.back-button {
  display: inline-block;
  padding: 8px 12px;
  margin-bottom: 20px;
  color: #555;
  cursor: pointer;
  font-weight: bold;
  transition: color 0.2s;
}

.back-button:hover {
  color: #1976D2;
}

.meter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  color: white;
  margin-bottom: 25px;
}

.meter-header.水表 {
  background-color: #2196F3;
}

.meter-header.电表 {
  background-color: #FF9800;
}

.meter-header.气表 {
  background-color: #4CAF50;
}

.meter-header h1 {
  margin: 0;
  font-size: 24px;
}

.meter-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 14px;
}

.meter-status.normal {
  background-color: rgba(255, 255, 255, 0.2);
}

.meter-status.warning {
  background-color: #FFC107;
  color: #333;
}

.meter-status.error {
  background-color: #F44336;
}

.info-card, .chart-card, .readings-card {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 25px;
}

.info-card h2, .chart-card h2, .readings-card h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.info-item {
  padding: 10px;
}

.info-item .label {
  color: #777;
  font-size: 14px;
  margin-bottom: 5px;
}

.info-item .value {
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-tabs {
  display: flex;
}

.chart-tab {
  padding: 8px 16px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  color: #777;
}

.chart-tab.active {
  border-bottom: 2px solid #1976D2;
  color: #1976D2;
  font-weight: bold;
}

.chart-container {
  height: 300px;
  padding: 20px 0;
}

.chart-line {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 100%;
  position: relative;
}

.chart-line:after {
  content: '';
  position: absolute;
  width: 100%;
  height: 1px;
  background-color: #ddd;
  bottom: 50px;
}

.chart-point {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  width: 16%;
}

.point {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #1976D2;
  margin-bottom: 10px;
  position: relative;
  z-index: 1;
}

.point-value {
  position: absolute;
  top: -25px;
  font-size: 12px;
  color: #333;
  font-weight: bold;
}

.date-label {
  position: absolute;
  bottom: 0;
  font-size: 12px;
  color: #777;
  width: 80px;
  text-align: center;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  text-align: left;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f5f5f5;
  color: #333;
  font-weight: bold;
}

tr:hover {
  background-color: #f9f9f9;
}

.error-state {
  text-align: center;
  padding: 80px 20px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-message {
  font-size: 18px;
  color: #777;
  margin-bottom: 20px;
}

.back-btn {
  padding: 10px 20px;
  background-color: #1976D2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.back-btn:hover {
  background-color: #1565C0;
}
</style>
