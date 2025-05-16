<script setup lang="ts">
// 模拟账单数据
import {Download} from "@element-plus/icons-vue";

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

// 获取状态标签类型
const getStatusTagType = (bill:any) => {
    if (bill.isPaid) return 'success';
    return 'warning';
};

definePageMeta({
    layout: "home"
});
</script>

<template>
    <NuxtLayout>
        <el-container class="bills-container">
            <el-header style="display: flex;align-items: center;justify-content: center">
                <h1 class="page-title">账单中心</h1>
            </el-header>

            <el-main>
                <!-- 筛选工具栏 -->
                <el-card class="filter-card" shadow="hover">
                    <el-row :gutter="20">
                        <el-col :xs="24" :sm="12" :md="8">
                            <el-form-item label="年份:">
                                <el-select v-model="filterOptions.year" size="small" style="width: 120px">
                                    <el-option
                                        v-for="year in yearOptions"
                                        :key="year"
                                        :label="`${year}年`"
                                        :value="year"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>

                        <el-col :xs="24" :sm="12" :md="8">
                            <el-form-item label="状态:">
                                <el-select v-model="filterOptions.status" size="small" style="width: 120px">
                                    <el-option
                                        v-for="status in statusOptions"
                                        :key="status"
                                        :label="status"
                                        :value="status"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-card>

                <!-- 账单卡片列表 -->
                <el-empty v-if="filteredBills.length === 0" description="没有符合条件的账单记录" />

                <div v-else class="bills-list">
                    <el-card
                        v-for="bill in filteredBills"
                        :key="bill.id"
                        class="bill-card"
                        shadow="hover"
                    >
                        <template #header>
                            <div class="bill-header">
                                <span class="bill-month">{{ getFormattedDate(bill.month) }}账单</span>
                                <el-tag :type="getStatusTagType(bill)" size="small" effect="light">
                                    {{ bill.status }} {{ bill.isPaid ? '（已支付）' : '（未支付）' }}
                                </el-tag>
                            </div>
                        </template>

                        <el-row :gutter="20">
                            <!-- 账单详情 -->
                            <el-col :xs="24" :sm="24" :md="14">
                                <div class="bill-details">
                                    <el-descriptions :column="1" size="small" border>
                                        <el-descriptions-item label="水表读数">
                                            <div class="reading-info">
                                                <span>{{ bill.water.reading }} m³</span>
                                                <el-tag
                                                    :type="bill.water.usage > 0 ? 'danger' : 'success'"
                                                    size="small"
                                                    effect="plain"
                                                >
                                                    {{ bill.water.usage > 0 ? '+' : '' }}{{ bill.water.usage }} m³
                                                </el-tag>
                                            </div>
                                        </el-descriptions-item>

                                        <el-descriptions-item label="电表读数">
                                            <div class="reading-info">
                                                <span>{{ bill.electricity.reading }} kWh</span>
                                                <el-tag
                                                    :type="bill.electricity.usage > 0 ? 'danger' : 'success'"
                                                    size="small"
                                                    effect="plain"
                                                >
                                                    {{ bill.electricity.usage > 0 ? '+' : '' }}{{ bill.electricity.usage }} kWh
                                                </el-tag>
                                            </div>
                                        </el-descriptions-item>

                                        <el-descriptions-item label="气表读数">
                                            <div class="reading-info">
                                                <span>{{ bill.gas.reading }} m³</span>
                                                <el-tag
                                                    :type="bill.gas.usage > 0 ? 'danger' : 'success'"
                                                    size="small"
                                                    effect="plain"
                                                >
                                                    {{ bill.gas.usage > 0 ? '+' : '' }}{{ bill.gas.usage }} m³
                                                </el-tag>
                                            </div>
                                        </el-descriptions-item>
                                    </el-descriptions>
                                </div>
                            </el-col>

                            <!-- 费用明细 -->
                            <el-col :xs="24" :sm="24" :md="10">
                                <el-card class="cost-summary" shadow="never">
                                    <template #header>
                                        <div class="cost-header">
                                            <span>费用明细</span>
                                            <span class="total-cost">总计: ¥{{ bill.totalCost.toFixed(2) }}</span>
                                        </div>
                                    </template>

                                    <el-row class="cost-item">
                                        <el-col :span="12">水费</el-col>
                                        <el-col :span="12" class="cost-value">¥{{ bill.water.cost.toFixed(2) }}</el-col>
                                    </el-row>

                                    <el-row class="cost-item">
                                        <el-col :span="12">电费</el-col>
                                        <el-col :span="12" class="cost-value">¥{{ bill.electricity.cost.toFixed(2) }}</el-col>
                                    </el-row>

                                    <el-row class="cost-item">
                                        <el-col :span="12">气费</el-col>
                                        <el-col :span="12" class="cost-value">¥{{ bill.gas.cost.toFixed(2) }}</el-col>
                                    </el-row>
                                </el-card>
                            </el-col>
                        </el-row>

                        <!-- 账单底部 -->
                        <el-divider />
                        <div class="bill-footer">
                            <el-text type="info" size="small">
                                {{ bill.isPaid ? `支付日期: ${bill.paidDate}` : '未支付' }}
                            </el-text>

                            <el-button
                                type="primary"
                                size="small"
                                @click="downloadPDF(bill.id)"
                                :icon="Download"
                            >
                                下载PDF账单
                            </el-button>
                        </div>
                    </el-card>
                </div>
            </el-main>
        </el-container>
    </NuxtLayout>
</template>

<style scoped>
.page-title {
    margin-top: 30px;
    color: #333;
    text-align: center;
    font-size: 40px;
}
.bills-container {
    max-width: 1000px;
    margin: 0 auto;
}

.el-header {
    padding: 20px 0;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.filter-card {
    margin-bottom: 20px;
}

.bills-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.bill-card {
    transition: all 0.3s;
}

.bill-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.bill-month {
    font-weight: bold;
    font-size: 16px;
}

.bill-details {
    margin-bottom: 15px;
}

.reading-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.cost-summary {
    height: 100%;
}

.cost-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.cost-item {
    margin-bottom: 8px;
    padding: 8px 0;
    border-bottom: 1px dashed #ebeef5;
}

.cost-item:last-child {
    border-bottom: none;
    margin-bottom: 0;
}

.cost-value {
    text-align: right;
    font-weight: bold;
}

.total-cost {
    font-weight: bold;
    color: #f56c6c;
}

.bill-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

@media (max-width: 768px) {
    .bill-card {
        margin-bottom: 15px;
    }

    .bill-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;
    }
}
</style>
