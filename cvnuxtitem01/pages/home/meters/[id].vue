<script setup lang="ts">

import {useHomeMeterService} from "~/services/home/meter";
import type {MeterHome} from "~/types/home/meters/type";
import {formatDateTime} from "~/utils/time";

const route = useRoute();
const router = useRouter();

// 获取表计ID
const meterId = computed(() => Number(route.params.id));
const meterReading = ref<MeterHome>({
    meter: {
        id: -1,
        type: 'water',
        location: '',
        installDate: null,
        readings: []
    },
    lastReading: -1,
    unit: '',
    status: '正常',
})

const fetchData = async () => {
    useHomeMeterService()
        .apiGetMererList(meterId.value)
        .then(response => {
            // console.log(response.data);
            Object.assign(meterReading.value, response.data);
            console.log(meterReading.value);
        })
}


onMounted(() => {
    fetchData()
    console.log(route.params.id)
})

// 图表数据
const chartData = computed(() => {
    if (!meterReading.value || !meterReading.value.meter.readings) {
        return { dates: [], values: [], costs: [] };
    }

    return {
        dates: meterReading.value.meter.readings.map(r => formatDateTime(r.shotTime)),
        values: meterReading.value.meter.readings.map(r => r.value),
        costs: meterReading.value.meter.readings.map(r => r.cost)
    };
});

// 当前选中的图表类型
const activeTab = ref('readings');

// 返回到表计列表页
const goBack = () => {
    router.push('/home/meters');
};

// 计算图表数据
const chartOption = computed(() => {
    return {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: chartData.value.dates
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: activeTab.value === 'readings' ? chartData.value.values : chartData.value.costs,
                type: 'line',
                smooth: true
            }
        ]
    };
});

// 表格列配置
const tableColumns = [
    {
        prop: 'date',
        label: '日期',
        width: '180'
    },
    {
        prop: 'value',
        label: '读数',
        formatter: (row: any) => `${row.value} ${meterReading.value?.unit}`
    },
    {
        prop: 'cost',
        label: '费用',
        formatter: (row: any) => `¥${row.cost.toFixed(2)}`
    }
];

// 状态标签类型
const getStatusTagType = (status: string) => {
    switch (status) {
        case '正常': return 'success';
        case '需要检查': return 'warning';
        default: return 'danger';
    }
};

// 表计类型对应颜色
const getMeterColor = (type: string) => {
    switch (type) {
        case '水表': return '#2196F3';
        case '电表': return '#FF9800';
        case '气表': return '#4CAF50';
        default: return '#909399';
    }
};

definePageMeta({
    layout: "home"
})
</script>

<template>
    <NuxtLayout>
        <div class="meter-detail-container">
            <el-page-header @back="goBack" :title="'返回表计列表'" />

            <el-empty v-if="!meterReading" description="未找到该表计信息">
                <el-button type="primary" @click="goBack">返回表计列表</el-button>
            </el-empty>

            <template v-else>
                <!-- 表计头部信息 -->
                <el-card
                    class="meter-header-card"
                    :style="{ borderTop: `5px solid ${getMeterColor(meterReading.meter.type)}` }"
                    shadow="hover"
                >
                    <div class="meter-header-content">
                        <div>
                            <h2>{{ meterReading.meter.type }} - {{ meterReading.meter.location }}</h2>
<!--                            <p>序列号: {{ meter.serialNumber }}</p>-->
                        </div>
                        <el-tag :type="getStatusTagType(meterReading.status)" size="large">
                            {{ meterReading.status }}
                        </el-tag>
                    </div>
                </el-card>

                <!-- 表计基本信息 -->
                <el-card class="info-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>基本信息</span>
                        </div>
                    </template>
                    <el-descriptions :column="3" border>
<!--                        <el-descriptions-item label="表计型号">{{ meter.model }}</el-descriptions-item>-->
<!--                        <el-descriptions-item label="制造商">{{ meter.manufacturer }}</el-descriptions-item>-->
                        <el-descriptions-item label="安装日期">{{ meterReading.meter.installDate }}</el-descriptions-item>
<!--                        <el-descriptions-item label="最近检修">{{ meter.lastMaintenance }}</el-descriptions-item>-->
                        <el-descriptions-item label="最近读数">{{ meterReading.lastReading }} {{ meterReading.unit }}</el-descriptions-item>
                        <el-descriptions-item label="位置">{{ meterReading.meter.location }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <!-- 历史数据图表 -->
                <el-card class="chart-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>历史数据</span>
                            <el-radio-group v-model="activeTab" size="small">
                                <el-radio-button value="readings">读数变化</el-radio-button>
                                <el-radio-button value="costs">费用变化</el-radio-button>
                            </el-radio-group>
                        </div>
                    </template>
                    <div class="chart-container">
                        <el-skeleton :loading="false" animated>
                            <template #default>
                                <div class="echarts-container" style="height: 300px;">
                                    <!-- 这里需要引入 ECharts 或其他图表库 -->
                                    <el-empty v-if="chartData.dates.length === 0" description="暂无图表数据" />
                                    <div v-else>
                                        <!-- 使用 v-chart 或直接初始化 echarts -->
                                        <div style="display: flex; height: 250px; align-items: flex-end; justify-content: space-between;">
                                            <div
                                                v-for="(date, index) in chartData.dates"
                                                :key="date"
                                                class="chart-column"
                                            >
                                                <div class="chart-value">
                                                    {{ activeTab === 'readings' ? chartData.values[index] : '¥' + chartData.costs[index].toFixed(2) }}
                                                </div>
                                                <el-tooltip :content="`${date}: ${activeTab === 'readings' ? chartData.values[index] + ' ' + meterReading.unit : '¥' + chartData.costs[index].toFixed(2)}`">
                                                    <div
                                                        class="chart-bar"
                                                        :style="{
                                                                  height: activeTab === 'readings'
                                                                    ? `${(chartData.values[index] / Math.max(...chartData.values)) * 200}px`
                                                                    : `${(chartData.costs[index] / Math.max(...chartData.costs)) * 200}px`,
                                                                  backgroundColor: getMeterColor(meterReading.meter.type)
                                                                }"
                                                    ></div>
                                                </el-tooltip>
                                                <div class="chart-date">{{ date }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </el-skeleton>
                    </div>
                </el-card>

                <!-- 历史读数表格 -->
                <el-card class="readings-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <span>历史读数记录</span>
                        </div>
                    </template>
                    <el-table
                        :data="meterReading.meter.readings"
                        stripe
                        style="width: 100%"
                        :default-sort="{ prop: 'date', order: 'descending' }"
                    >
                        <el-table-column prop="shotTime" label="日期" sortable >
                            <template #default="{ row }">
                                {{ formatDateTime(row.shotTime) }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="value" :label="`读数 (${meterReading.unit})`" sortable />
                        <el-table-column
                            prop="cost"
                            label="费用 (¥)"
                            sortable
                            :formatter="(row) => row.cost.toFixed(2)"
                        />
                    </el-table>
                </el-card>
            </template>
        </div>
    </NuxtLayout>
</template>

<style scoped>
.meter-detail-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.meter-header-card {
    margin-top: 20px;
    margin-bottom: 20px;
}

.meter-header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.meter-header-content h2 {
    margin: 0;
    font-size: 20px;
}

.meter-header-content p {
    margin: 5px 0 0;
    color: #909399;
    font-size: 14px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.info-card,
.chart-card,
.readings-card {
    margin-bottom: 20px;
}

.chart-container {
    padding: 10px 0;
}

.chart-column {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: calc(100% / 7);
    min-width: 60px;
}

.chart-bar {
    width: 30px;
    border-radius: 4px;
    transition: all 0.3s;
    cursor: pointer;
}

.chart-bar:hover {
    opacity: 0.8;
}

.chart-value {
    font-size: 12px;
    margin-bottom: 5px;
}

.chart-date {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
}

@media screen and (max-width: 768px) {
    .chart-column {
        min-width: 40px;
    }

    .chart-bar {
        width: 20px;
    }
}
</style>
