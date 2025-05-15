<script setup lang="ts">
// 模拟账单数据
import {ArrowLeft, Download} from "@element-plus/icons-vue";

const bills = ref([
    {
        id: 1,
        month: '2025-05',
        water: { reading: 123.5, cost: 78.4, lastReading: 118.3, usage: 5.2 },
        electricity: { reading: 568.7, cost: 195.6, lastReading: 552.9, usage: 15.8 },
        gas: { reading: 89.2, cost: 167.3, lastReading: 91.3, usage: -2.1 },
        totalCost: 441.3,
        status: '已出账',
        isPaid: true,
        paidDate: '2025-05-15'
    },
    {
        id: 2,
        month: '2025-04',
        water: { reading: 118.3, cost: 73.2, lastReading: 114.1, usage: 4.2 },
        electricity: { reading: 552.9, cost: 190.1, lastReading: 538.4, usage: 14.5 },
        gas: { reading: 91.3, cost: 172.5, lastReading: 87.6, usage: 3.7 },
        totalCost: 435.8,
        status: '已出账',
        isPaid: true,
        paidDate: '2025-04-15'
    },
    {
        id: 3,
        month: '2025-03',
        water: { reading: 114.1, cost: 71.8, lastReading: 110.5, usage: 3.6 },
        electricity: { reading: 538.4, cost: 185.3, lastReading: 525.2, usage: 13.2 },
        gas: { reading: 87.6, cost: 164.9, lastReading: 84.2, usage: 3.4 },
        totalCost: 422.0,
        status: '已出账',
        isPaid: true,
        paidDate: '2025-03-15'
    },
    {
        id: 4,
        month: '2025-02',
        water: { reading: 110.5, cost: 69.5, lastReading: 107.2, usage: 3.3 },
        electricity: { reading: 525.2, cost: 180.9, lastReading: 512.5, usage: 12.7 },
        gas: { reading: 84.2, cost: 158.3, lastReading: 81.0, usage: 3.2 },
        totalCost: 408.7,
        status: '已出账',
        isPaid: true,
        paidDate: '2025-02-15'
    },
    {
        id: 5,
        month: '2023-01',
        water: { reading: 107.2, cost: 67.3, lastReading: 104.0, usage: 3.2 },
        electricity: { reading: 512.5, cost: 176.8, lastReading: 500.1, usage: 12.4 },
        gas: { reading: 81.0, cost: 152.2, lastReading: 78.0, usage: 3.0 },
        totalCost: 396.3,
        status: '已出账',
        isPaid: true,
        paidDate: '2023-01-15'
    }
]);

// 筛选选项
const filterOptions = ref({
    year: new Date().getFullYear(),
    status: '全部',
});

// 年份选项
const yearOptions = [2025, 2022, 2021];
// 状态选项
const statusOptions = ['全部', '已出账', '未出账', '已支付', '未支付'];

// 筛选账单
const filteredBills = computed(() => {
    let result = [...bills.value];

    // 按年份筛选
    if (filterOptions.value.year) {
        result = result.filter(bill => {
            const billYear = parseInt(bill.month.split('-')[0]);
            return billYear === filterOptions.value.year;
        });
    }

    // 按状态筛选
    if (filterOptions.value.status !== '全部') {
        if (filterOptions.value.status === '已支付') {
            result = result.filter(bill => bill.isPaid);
        } else if (filterOptions.value.status === '未支付') {
            result = result.filter(bill => !bill.isPaid);
        } else {
            result = result.filter(bill => bill.status === filterOptions.value.status);
        }
    }

    return result;
});

// 下载PDF
const downloadPDF = (billId: number) => {
    const bill = bills.value.find(b => b.id === billId);

    if (!bill) {
        ElMessage.error('账单不存在');
        return;
    }

    // 实际开发中应该调用后端API生成并下载PDF
    ElMessage.success(`正在下载 ${bill.month} 的账单PDF`);
};

// 获取月份名称
const getMonthName = (monthStr: string) => {
    const month = parseInt(monthStr.split('-')[1]);
    return `${month}月`;
};

// 获取实际应用中显示的日期（YYYY年MM月）
const getFormattedDate = (monthStr: string) => {
    const [year, month] = monthStr.split('-');
    return `${year}年${month}月`;
};

definePageMeta({
    layout: "home"
})
</script>

<template>
    <NuxtLayout>
        <div class="bills-page">
            <el-page-header :icon="ArrowLeft" title="账单中心">
                <template #content>
                    <div class="flex items-center">
                        <el-icon class="mr-2"><ElIconDocument /></el-icon>
                        <span class="text-large font-bold">账单中心</span>
                    </div>
                </template>
            </el-page-header>

            <!-- 筛选工具栏 -->
            <el-card class="filter-card" shadow="hover">
                <el-row :gutter="20">
                    <el-col :span="12" :xs="24">
                        <div class="filter-item">
                            <span class="filter-label">年份:</span>
                            <el-select v-model="filterOptions.year" placeholder="选择年份" class="filter-select">
                                <el-option v-for="year in yearOptions" :key="year" :label="`${year}年`" :value="year" />
                            </el-select>
                        </div>
                    </el-col>
                    <el-col :span="12" :xs="24">
                        <div class="filter-item">
                            <span class="filter-label">状态:</span>
                            <el-select v-model="filterOptions.status" placeholder="选择状态" class="filter-select">
                                <el-option v-for="status in statusOptions" :key="status" :label="status" :value="status" />
                            </el-select>
                        </div>
                    </el-col>
                </el-row>
            </el-card>

            <!-- 账单卡片列表 -->
            <div class="bills-list">
                <el-empty v-if="filteredBills.length === 0" description="没有符合条件的账单记录" />

                <el-card v-for="bill in filteredBills" :key="bill.id" class="bill-card" shadow="hover">
                    <template #header>
                        <div class="bill-header">
                            <div class="bill-month">{{ getFormattedDate(bill.month) }}账单</div>
                            <div>
                                <el-tag :type="bill.isPaid ? 'success' : 'danger'" effect="plain">
                                    {{ bill.status }}
                                </el-tag>
                                <el-tag :type="bill.isPaid ? 'success' : 'warning'" class="ml-2">
                                    {{ bill.isPaid ? '已支付' : '未支付' }}
                                </el-tag>
                            </div>
                        </div>
                    </template>

                    <el-row :gutter="20">
                        <el-col :lg="16" :md="24">
                            <div class="bill-details">
                                <el-descriptions :column="1" border>
                                    <el-descriptions-item label="水表读数">
                                        <span>{{ bill.water.reading }} m³</span>
                                        <el-tag :type="bill.water.usage > 0 ? 'warning' : 'success'" size="small" class="ml-2">
                                            {{ bill.water.usage > 0 ? '+' : '' }}{{ bill.water.usage }} m³
                                        </el-tag>
                                    </el-descriptions-item>

                                    <el-descriptions-item label="电表读数">
                                        <span>{{ bill.electricity.reading }} kWh</span>
                                        <el-tag :type="bill.electricity.usage > 0 ? 'warning' : 'success'" size="small" class="ml-2">
                                            {{ bill.electricity.usage > 0 ? '+' : '' }}{{ bill.electricity.usage }} kWh
                                        </el-tag>
                                    </el-descriptions-item>

                                    <el-descriptions-item label="气表读数">
                                        <span>{{ bill.gas.reading }} m³</span>
                                        <el-tag :type="bill.gas.usage > 0 ? 'warning' : 'success'" size="small" class="ml-2">
                                            {{ bill.gas.usage > 0 ? '+' : '' }}{{ bill.gas.usage }} m³
                                        </el-tag>
                                    </el-descriptions-item>
                                </el-descriptions>

                                <div class="total-cost">
                                    <span class="cost-label">总费用:</span>
                                    <span class="cost-value">¥{{ bill.totalCost.toFixed(2) }}</span>
                                </div>
                            </div>
                        </el-col>

                        <el-col :lg="8" :md="24">
                            <el-divider direction="vertical" class="hidden-md-and-down" />
                            <el-divider class="hidden-lg-and-up" />

                            <div class="bill-breakdown">
                                <el-card shadow="never" class="fee-card">
                                    <template #header>
                                        <div class="fee-header">水费</div>
                                    </template>
                                    <div class="fee-amount">¥{{ bill.water.cost.toFixed(2) }}</div>
                                </el-card>

                                <el-card shadow="never" class="fee-card">
                                    <template #header>
                                        <div class="fee-header">电费</div>
                                    </template>
                                    <div class="fee-amount">¥{{ bill.electricity.cost.toFixed(2) }}</div>
                                </el-card>

                                <el-card shadow="never" class="fee-card">
                                    <template #header>
                                        <div class="fee-header">气费</div>
                                    </template>
                                    <div class="fee-amount">¥{{ bill.gas.cost.toFixed(2) }}</div>
                                </el-card>
                            </div>
                        </el-col>
                    </el-row>

                    <el-divider />

                    <div class="bill-footer">
                        <div class="bill-date">
                            <el-tag type="info" effect="plain">
                                {{ bill.isPaid ? `支付日期: ${bill.paidDate}` : '未支付' }}
                            </el-tag>
                        </div>
                        <el-button type="primary" @click="downloadPDF(bill.id)" :icon="Download">
                            下载PDF账单
                        </el-button>
                    </div>
                </el-card>
            </div>
        </div>
    </NuxtLayout>
</template>

<style scoped>
.bills-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.filter-card {
    margin: 20px 0;
}

.filter-item {
    display: flex;
    align-items: center;
    margin: 10px 0;
}

.filter-label {
    margin-right: 10px;
    font-weight: bold;
    color: var(--el-text-color-regular);
    width: 60px;
}

.filter-select {
    width: 150px;
}

.bills-list {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.bill-card {
    margin-bottom: 0;
}

.bill-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.bill-month {
    font-size: 18px;
    font-weight: bold;
}

.bill-details {
    margin-bottom: 20px;
}

.total-cost {
    margin-top: 20px;
    padding: 12px;
    background-color: var(--el-color-primary-light-9);
    border-radius: 4px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.cost-label {
    font-weight: bold;
    color: var(--el-text-color-regular);
}

.cost-value {
    font-size: 20px;
    font-weight: bold;
    color: var(--el-color-danger);
}

.bill-breakdown {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.fee-card {
    margin-bottom: 10px;
    border: 1px solid var(--el-border-color-light);
}

.fee-header {
    font-size: 14px;
    color: var(--el-text-color-regular);
}

.fee-amount {
    font-size: 16px;
    font-weight: bold;
    color: var(--el-text-color-primary);
    text-align: center;
}

.bill-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

@media (max-width: 768px) {
    .bill-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: 15px;
    }
}

/* 用于处理Element Plus响应式布局 */
.hidden-lg-and-up {
    display: none;
}

@media (max-width: 992px) {
    .hidden-lg-and-up {
        display: block;
    }

    .hidden-md-and-down {
        display: none !important;
    }
}
</style>
