<script setup lang="ts">
// 模拟表计数据
const meters = ref([
    {
        id: 1,
        type: '水表',
        location: '厨房',
        model: 'WM-100A',
        installDate: '2022-01-15',
        lastReading: 123.5,
        unit: 'm³',
        status: '正常'
    },
    {
        id: 2,
        type: '电表',
        location: '配电室',
        model: 'EM-200B',
        installDate: '2021-11-20',
        lastReading: 568.7,
        unit: 'kWh',
        status: '正常'
    },
    {
        id: 3,
        type: '气表',
        location: '厨房',
        model: 'GM-150C',
        installDate: '2022-03-05',
        lastReading: 89.2,
        unit: 'm³',
        status: '正常'
    },
    {
        id: 4,
        type: '水表',
        location: '卫生间',
        model: 'WM-100A',
        installDate: '2022-02-10',
        lastReading: 78.3,
        unit: 'm³',
        status: '需要检查'
    }
]);

// 筛选条件
const typeFilter = ref('全部');
const statusFilter = ref('全部');

// 表计类型选项
const typeOptions = ['全部', '水表', '电表', '气表'];
const statusOptions = ['全部', '正常', '需要检查', '故障'];

// 筛选后的表计列表
const filteredMeters = computed(() => {
    return meters.value.filter(meter => {
        const typeMatch = typeFilter.value === '全部' || meter.type === typeFilter.value;
        const statusMatch = statusFilter.value === '全部' || meter.status === statusFilter.value;
        return typeMatch && statusMatch;
    });
});

// 查看详情
const router = useRouter();
const viewMeterDetail = (meterId: number) => {
    router.push(`/home/meters/${meterId}`);
};

definePageMeta({
    layout: "home"
})
</script>

<template>
    <nuxt-layout>
        <div class="meters-page">
            <h1 class="page-title">我的表计</h1>

            <!-- 筛选区域 -->
            <div class="filters">
                <div class="filter-group">
                    <label>表计类型:</label>
                    <select v-model="typeFilter">
                        <option v-for="option in typeOptions" :key="option" :value="option">{{ option }}</option>
                    </select>
                </div>

                <div class="filter-group">
                    <label>状态:</label>
                    <select v-model="statusFilter">
                        <option v-for="option in statusOptions" :key="option" :value="option">{{ option }}</option>
                    </select>
                </div>
            </div>

            <!-- 表计列表 -->
            <div class="meters-list">
                <div v-for="meter in filteredMeters" :key="meter.id" class="meter-card" @click="viewMeterDetail(meter.id)">
                    <div class="meter-type" :class="meter.type.toLowerCase()">{{ meter.type }}</div>
                    <div class="meter-details">
                        <h3>{{ meter.location }}</h3>
                        <div class="meter-info">
                            <p><span>型号:</span> {{ meter.model }}</p>
                            <p><span>安装日期:</span> {{ meter.installDate }}</p>
                            <p><span>最近读数:</span> {{ meter.lastReading }} {{ meter.unit }}</p>
                        </div>
                        <div class="meter-status" :class="meter.status === '正常' ? 'normal' : meter.status === '需要检查' ? 'warning' : 'error'">
                            {{ meter.status }}
                        </div>
                    </div>
                    <div class="view-details">
                        <span>查看详情</span>
                        <i class="arrow-icon">→</i>
                    </div>
                </div>
            </div>
            <div>
                <!--            <NuxtPage/>-->
            </div>

            <!-- 如果没有表计数据，显示空状态 -->
            <div v-if="filteredMeters.length === 0" class="no-data">
                没有找到符合条件的表计
            </div>
        </div>
    </nuxt-layout>

</template>

<style scoped>
.meters-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
}

.page-title {
    color: #333;
    margin-bottom: 30px;
    text-align: center;
}

/* 筛选区域样式 */
.filters {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
    background-color: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group {
    display: flex;
    align-items: center;
}

.filter-group label {
    margin-right: 10px;
    color: #555;
}

.filter-group select {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    background-color: #f9f9f9;
    cursor: pointer;
}

/* 表计列表样式 */
.meters-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
}

.meter-card {
    background-color: white;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    cursor: pointer;
}

.meter-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.meter-type {
    padding: 12px 20px;
    color: white;
    font-weight: bold;
}

.meter-type.水表 {
    background-color: #2196F3;
}

.meter-type.电表 {
    background-color: #FF9800;
}

.meter-type.气表 {
    background-color: #4CAF50;
}

.meter-details {
    padding: 15px 20px;
}

.meter-details h3 {
    margin-top: 0;
    margin-bottom: 15px;
    color: #333;
}

.meter-info p {
    margin: 8px 0;
    color: #666;
}

.meter-info span {
    font-weight: bold;
    color: #555;
}

.meter-status {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 14px;
    margin-top: 10px;
}

.meter-status.normal {
    background-color: #E8F5E9;
    color: #4CAF50;
}

.meter-status.warning {
    background-color: #FFF8E1;
    color: #FF9800;
}

.meter-status.error {
    background-color: #FFEBEE;
    color: #F44336;
}

.view-details {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
    background-color: #f5f5f5;
    border-top: 1px solid #eee;
    color: #1976D2;
    font-weight: bold;
}

.arrow-icon {
    font-size: 18px;
}

/* 空状态样式 */
.no-data {
    text-align: center;
    padding: 40px;
    color: #999;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-top: 20px;
}
</style>
