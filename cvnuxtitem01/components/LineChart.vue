<template>
    <div class="line-chart" ref="chartContainer"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as echarts from 'echarts/core';
import { LineChart } from 'echarts/charts';
import {
    TitleComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

// 注册必要的组件
echarts.use([
    TitleComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent,
    LineChart,
    CanvasRenderer
]);

const props = defineProps({
    chartData: {
        type: Object,
        required: true
    },
    chartOptions: {
        type: Object,
        default: () => ({})
    }
});

const chartContainer = ref(null);
let chartInstance = null;

// 初始化图表
const initChart = () => {
    if (!chartContainer.value) return;

    chartInstance = echarts.init(chartContainer.value);
    updateChart();

    // 窗口大小变化时调整图表尺寸
    window.addEventListener('resize', handleResize);
};

// 更新图表
const updateChart = () => {
    if (!chartInstance) return;

    // 将 Chart.js 数据格式转换为 ECharts 格式
    const series = props.chartData.datasets.map(dataset => ({
        name: dataset.label,
        type: 'line',
        data: dataset.data,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
            color: dataset.borderColor || dataset.backgroundColor
        },
        lineStyle: {
            width: 2,
            color: dataset.borderColor
        },
        areaStyle: dataset.fill ? {
            opacity: 0.3,
            color: dataset.backgroundColor
        } : undefined
    }));

    const option = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: props.chartData.datasets.map(dataset => dataset.label)
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: props.chartData.labels
        },
        yAxis: {
            type: 'value'
        },
        series,
        ...props.chartOptions
    };

    chartInstance.setOption(option);
};

// 处理窗口大小变化
const handleResize = () => {
    chartInstance && chartInstance.resize();
};

// 监听数据变化
watch(() => props.chartData, () => {
    nextTick(() => updateChart());
}, { deep: true });

watch(() => props.chartOptions, () => {
    nextTick(() => updateChart());
}, { deep: true });

// 组件挂载时初始化图表
onMounted(() => {
    nextTick(() => {
        initChart();
    });
});

// 组件卸载时销毁图表
onUnmounted(() => {
    if (chartInstance) {
        chartInstance.dispose();
        window.removeEventListener('resize', handleResize);
    }
});
</script>

<style scoped>
.line-chart {
    height: 300px;
    width: 100%;
}
</style>
