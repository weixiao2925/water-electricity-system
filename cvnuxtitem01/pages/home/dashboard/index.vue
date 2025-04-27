<script setup lang="ts">
// 模拟数据
const meterTypes = ['水表', '电表', '气表'];
const overviewData = [
    { type: '水表', current: 123.5, unit: 'm³', change: 5.2, changePercent: 2.3, cost: 78.4 },
    { type: '电表', current: 568.7, unit: 'kWh', change: 15.8, changePercent: 3.1, cost: 195.6 },
    { type: '气表', current: 89.2, unit: 'm³', change: -2.1, changePercent: -2.5, cost: 167.3 }
];

// 最近5次读数数据
const recentReadings = [
    { id: 1, type: '水表', reading: 123.5, time: '2023-05-01', cost: 78.4 },
    { id: 2, type: '电表', reading: 568.7, time: '2023-05-01', cost: 195.6 },
    { id: 3, type: '气表', reading: 89.2, time: '2023-05-01', cost: 167.3 },
    { id: 4, type: '水表', reading: 118.3, time: '2023-04-01', cost: 73.2 },
    { id: 5, type: '电表', reading: 552.9, time: '2023-04-01', cost: 190.1 },
];

// 月度数据，用于折线图
const monthlyData = {
    months: ['1月', '2月', '3月', '4月', '5月', '6月'],
    water: [85, 88, 92, 98, 110, 123.5],
    electricity: [498, 510, 525, 540, 555, 568.7],
    gas: [78, 80, 83, 85, 87, 89.2]
};

// 当前选中的图表类型
const selectedChartType = ref('水表');

const setChartType = (type:any) => {
    selectedChartType.value = type;
};

definePageMeta({
    layout: "home"
})
</script>

<template>
    <NuxtLayout>
        <div class="dashboard">
            <h1 class="dashboard-title">仪表盘概览</h1>

            <!-- 概览卡片区域 -->
            <div class="overview-cards">
                <div v-for="item in overviewData" :key="item.type" class="card">
                    <h2>{{ item.type }}</h2>
                    <div class="reading">
                        <span class="value">{{ item.current }}</span>
                        <span class="unit">{{ item.unit }}</span>
                    </div>
                    <div class="change" :class="{ 'positive': item.change > 0, 'negative': item.change < 0 }">
                        <span>{{ item.change > 0 ? '+' : '' }}{{ item.change }} ({{ item.changePercent }}%)</span>
                    </div>
                    <div class="cost">
                        <span>本月费用: ¥{{ item.cost.toFixed(2) }}</span>
                    </div>
                </div>
            </div>

            <!-- 月度折线图区域 -->
            <div class="chart-section">
                <h2>月度用量趋势</h2>
                <div class="chart-tabs">
                    <div
                        v-for="type in meterTypes"
                        :key="type"
                        class="chart-tab"
                        :class="{ 'active': selectedChartType === type }"
                        @click="setChartType(type)"
                    >
                        {{ type }}
                    </div>
                </div>
                <div class="chart-container">
                    <!-- 这里放折线图，实际项目中可以使用ECharts等库 -->
                    <div class="mock-chart">
                        <div class="chart-line" :class="selectedChartType.toLowerCase()">
                            <div v-for="(month, index) in monthlyData.months" :key="index" class="chart-point">
                                <div class="point"></div>
                                <span class="month-label">{{ month }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 最近5次读数表格 -->
            <div class="recent-readings">
                <h2>最近读数记录</h2>
                <table>
                    <thead>
                    <tr>
                        <th>表类型</th>
                        <th>读数</th>
                        <th>日期</th>
                        <th>费用(¥)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="reading in recentReadings" :key="reading.id">
                        <td>{{ reading.type }}</td>
                        <td>{{ reading.reading }}</td>
                        <td>{{ reading.time }}</td>
                        <td>{{ reading.cost.toFixed(2) }}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </NuxtLayout>

</template>

<style scoped>
.dashboard {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
}

.dashboard-title {
    color: #333;
    margin-bottom: 30px;
    text-align: center;
}

/* 概览卡片样式 */
.overview-cards {
    display: flex;
    gap: 20px;
    margin-bottom: 40px;
    flex-wrap: wrap;
}

.card {
    flex: 1;
    min-width: 250px;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background-color: white;
}

.card h2 {
    margin-top: 0;
    color: #333;
    font-size: 18px;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}

.reading {
    margin: 20px 0;
}

.value {
    font-size: 28px;
    font-weight: bold;
    color: #333;
}

.unit {
    font-size: 16px;
    color: #666;
    margin-left: 5px;
}

.change {
    font-size: 14px;
    margin: 10px 0;
}

.positive {
    color: #4CAF50;
}

.negative {
    color: #F44336;
}

.cost {
    font-size: 16px;
    font-weight: bold;
    color: #333;
}

/* 折线图区域样式 */
.chart-section {
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 40px;
}

.chart-tabs {
    display: flex;
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
}

.chart-tab {
    padding: 10px 20px;
    cursor: pointer;
    color: #666;
}

.chart-tab.active {
    color: #1976D2;
    border-bottom: 2px solid #1976D2;
    font-weight: bold;
}

.chart-container {
    height: 300px;
}

/* 模拟折线图样式 */
.mock-chart {
    height: 100%;
    display: flex;
    align-items: flex-end;
}

.chart-line {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    width: 100%;
    height: 80%;
    position: relative;
}

.chart-line:after {
    content: '';
    position: absolute;
    width: 100%;
    height: 1px;
    background-color: #ddd;
    bottom: 0;
}

.chart-line.water .chart-point:nth-child(1) .point { height: 30%; }
.chart-line.water .chart-point:nth-child(2) .point { height: 35%; }
.chart-line.water .chart-point:nth-child(3) .point { height: 40%; }
.chart-line.water .chart-point:nth-child(4) .point { height: 45%; }
.chart-line.water .chart-point:nth-child(5) .point { height: 55%; }
.chart-line.water .chart-point:nth-child(6) .point { height: 65%; }

.chart-line.电表 .chart-point:nth-child(1) .point { height: 40%; }
.chart-line.电表 .chart-point:nth-child(2) .point { height: 50%; }
.chart-line.电表 .chart-point:nth-child(3) .point { height: 60%; }
.chart-line.电表 .chart-point:nth-child(4) .point { height: 70%; }
.chart-line.电表 .chart-point:nth-child(5) .point { height: 75%; }
.chart-line.电表 .chart-point:nth-child(6) .point { height: 80%; }

.chart-line.气表 .chart-point:nth-child(1) .point { height: 20%; }
.chart-line.气表 .chart-point:nth-child(2) .point { height: 25%; }
.chart-line.气表 .chart-point:nth-child(3) .point { height: 30%; }
.chart-line.气表 .chart-point:nth-child(4) .point { height: 35%; }
.chart-line.气表 .chart-point:nth-child(5) .point { height: 40%; }
.chart-line.气表 .chart-point:nth-child(6) .point { height: 45%; }

.chart-point {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 50px;
}

.point {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background-color: #1976D2;
    margin-bottom: 5px;
}

.month-label {
    font-size: 12px;
    color: #666;
}

/* 最近读数表格样式 */
.recent-readings {
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

table {
    width: 100%;
    border-collapse: collapse;
}

th, td {
    text-align: left;
    padding: 12px;
}

th {
    background-color: #f5f5f5;
    color: #333;
}

tr:nth-child(even) {
    background-color: #f9f9f9;
}

tr:hover {
    background-color: #f1f1f1;
}
</style>
