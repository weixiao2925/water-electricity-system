<script setup lang="ts">

import {useHomeMeterService} from "~/services/home/meter";
import type {MeterHome} from "~/types/home/meters/type";

const meterData = ref<MeterHome[]>([])
const type = ref<"water" | "electricity" | "gas" | "">("")
const status = ref<"全部" | "正常" | "需要检查" | "故障">("全部")
const typeOptions = [
    {
        label: '全部',
        value: '',
    }, {
        label: '水表',
        value: 'water'
    }, {
        label: '电表',
        value: 'electricity'
    }, {
        label: '气表',
        value: 'gas'
    }
]
const statusOption = [
    {
        label: '全部',
        value: ''
    }, {
        label: '正常',
        value: 'normal'
    }, {
        label: '需要检查',
        value: 'warning'
    }, {
        label: '故障',
        value: 'error'
    }
]

const getTypeLabel = (type: string) => {
    const typeMap: Record<string, string> = {
        'water': '水表',
        'electricity': '电表',
        'gas': '气表'
    };
    return typeMap[type] || type;
};

const getTypeClass = (type: string) => {
    return type;
};

const getStatusClass = (status: string) => {
    if (status === '正常') return 'normal';
    if (status === '需要检查') return 'warning';
    if (status === '故障') return 'error';
    return '';
};

const fetchData = () => {
    useHomeMeterService()
        .apiGetMeterSelf(type.value)
        .then(res => {
            // console.log(res.data);
            meterData.value = []
            Object.assign(meterData.value, res.data);
            // console.log(meterData.value)
        })
}

// 查看详情
const router = useRouter();
const viewMeterDetail = (meterId: number) => {
    router.push(`/home/meters/${meterId}`);
};

definePageMeta({
    layout: "home"
})
onMounted(()=>{
    fetchData()
})
</script>

<template>
    <nuxt-layout>
        <div class="meters-page">
            <h1 class="page-title">我的表计</h1>

            <div class="filters">
                <div class="label-option">表计类型:</div>
                <el-select v-model="type"  placeholder="请选择表计类型" clearable style="width: 150px;" @change="fetchData">
                    <el-option
                        v-for="option in typeOptions"
                        :key="option.label"
                        :label="option.label"
                        :value="option.value"
                    />
                </el-select>
                <div class="label-option">状态:</div>
                <el-select v-model="status" placeholder="请选择状态" clearable style="width: 150px;" @change="fetchData">
                    <el-option
                        v-for="option in statusOption"
                        :key = "option.label"
                        :label = "option.label"
                        :value = "option.value"
                    />
                </el-select>
            </div>

            <!-- 表计列表 -->
            <div class="meters-list">
                <div v-for="meter in meterData" :key="meter.meter.id" class="meter-card" @click="viewMeterDetail(meter.meter.id)">
                    <!-- 修改这里，确保正确显示类型并应用CSS类 -->
                    <div class="meter-type" :class="getTypeClass(meter.meter.type)">{{ getTypeLabel(meter.meter.type) }}</div>
                    <div class="meter-details">
                        <h3>{{ meter.meter.location }}</h3>
                        <div class="meter-info">
                            <p><span>安装日期:</span> {{ meter.meter.installDate }}</p>
                            <p><span>最近读数:</span> {{ meter.lastReading }} {{ meter.unit }}</p>
                        </div>
                        <div class="meter-status" :class="getStatusClass(meter.status)">
                            {{ meter.status }}
                        </div>
                    </div>
                    <div class="view-details">
                        <span>查看详情</span>
                        <i class="arrow-icon">→</i>
                    </div>
                </div>
            </div>

            <!-- 如果没有表计数据，显示空状态 -->
            <div v-if="meterData.length === 0" class="no-data">
                没有找到符合条件的表计
            </div>
        </div>
    </nuxt-layout>

</template>

<style scoped>
.label-option {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
}

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

.meter-type.water {
    background-color: #2196F3;
}

.meter-type.electricity {
    background-color: #FF9800;
}

.meter-type.gas {
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
