<script setup lang="ts">
// 模拟账单数据
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
        alert('账单不存在');
        return;
    }

    // 实际开发中应该调用后端API生成并下载PDF
    alert(`正在下载 ${bill.month} 的账单PDF`);
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
            <h1 class="bills-title">账单中心</h1>

            <!-- 筛选工具栏 -->
            <div class="filter-toolbar">
                <div class="filter-group">
                    <label>年份:</label>
                    <select v-model="filterOptions.year">
                        <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}年</option>
                    </select>
                </div>

                <div class="filter-group">
                    <label>状态:</label>
                    <select v-model="filterOptions.status">
                        <option v-for="status in statusOptions" :key="status" :value="status">{{ status }}</option>
                    </select>
                </div>
            </div>

            <!-- 账单卡片列表 -->
            <div class="bills-list">
                <div v-for="bill in filteredBills" :key="bill.id" class="bill-card">
                    <div class="bill-header">
                        <div class="bill-month">{{ getFormattedDate(bill.month) }}账单</div>
                        <div class="bill-status" :class="{'paid': bill.isPaid, 'unpaid': !bill.isPaid}">
                            {{ bill.status }} {{ bill.isPaid ? '（已支付）' : '（未支付）' }}
                        </div>
                    </div>

                    <div class="bill-content">
                        <div class="bill-details">
                            <div class="bill-detail-item">
                                <span class="detail-label">水表读数:</span>
                                <span class="detail-value">{{ bill.water.reading }} m³</span>
                                <span class="detail-change" :class="{'positive': bill.water.usage > 0, 'negative': bill.water.usage < 0}">
                                {{ bill.water.usage > 0 ? '+' : '' }}{{ bill.water.usage }} m³
                              </span>
                            </div>

                            <div class="bill-detail-item">
                                <span class="detail-label">电表读数:</span>
                                <span class="detail-value">{{ bill.electricity.reading }} kWh</span>
                                <span class="detail-change" :class="{'positive': bill.electricity.usage > 0, 'negative': bill.electricity.usage < 0}">
                                {{ bill.electricity.usage > 0 ? '+' : '' }}{{ bill.electricity.usage }} kWh
                              </span>
                            </div>

                            <div class="bill-detail-item">
                                <span class="detail-label">气表读数:</span>
                                <span class="detail-value">{{ bill.gas.reading }} m³</span>
                                <span class="detail-change" :class="{'positive': bill.gas.usage > 0, 'negative': bill.gas.usage < 0}">
                {{ bill.gas.usage > 0 ? '+' : '' }}{{ bill.gas.usage }} m³
              </span>
                            </div>

                            <div class="bill-detail-item total-cost">
                                <span class="detail-label">总费用:</span>
                                <span class="detail-value">¥{{ bill.totalCost.toFixed(2) }}</span>
                            </div>
                        </div>

                        <div class="bill-breakdown">
                            <div class="breakdown-item">
                                <div class="breakdown-title">水费</div>
                                <div class="breakdown-value">¥{{ bill.water.cost.toFixed(2) }}</div>
                            </div>

                            <div class="breakdown-item">
                                <div class="breakdown-title">电费</div>
                                <div class="breakdown-value">¥{{ bill.electricity.cost.toFixed(2) }}</div>
                            </div>

                            <div class="breakdown-item">
                                <div class="breakdown-title">气费</div>
                                <div class="breakdown-value">¥{{ bill.gas.cost.toFixed(2) }}</div>
                            </div>
                        </div>
                    </div>

                    <div class="bill-footer">
                        <div class="bill-date">
                            {{ bill.isPaid ? `支付日期: ${bill.paidDate}` : '未支付' }}
                        </div>

                        <button @click="downloadPDF(bill.id)" class="download-btn">
                            下载PDF账单
                        </button>
                    </div>
                </div>

                <div v-if="filteredBills.length === 0" class="no-bills">
                    没有符合条件的账单记录
                </div>
            </div>
        </div>
    </NuxtLayout>

</template>

<style scoped>
.bills-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
}

.bills-title {
    color: #333;
    margin-bottom: 30px;
    text-align: center;
}

/* 筛选工具栏样式 */
.filter-toolbar {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
    background-color: white;
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group {
    display: flex;
    align-items: center;
    gap: 10px;
}

.filter-group label {
    font-weight: bold;
    color: #555;
}

.filter-group select {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f9f9f9;
    cursor: pointer;
}

/* 账单卡片样式 */
.bills-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.bill-card {
    background-color: white;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.bill-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background-color: #f5f5f5;
    border-bottom: 1px solid #eee;
}

.bill-month {
    font-size: 18px;
    font-weight: bold;
    color: #333;
}

.bill-status {
    font-size: 14px;
    padding: 4px 10px;
    border-radius: 15px;
    background-color: #e0e0e0;
}

.bill-status.paid {
    background-color: #e8f5e9;
    color: #388e3c;
}

.bill-status.unpaid {
    background-color: #ffebee;
    color: #d32f2f;
}

.bill-content {
    padding: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    border-bottom: 1px solid #eee;
}

.bill-details {
    flex: 2;
    min-width: 300px;
}

.bill-detail-item {
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.detail-label {
    width: 100px;
    font-weight: bold;
    color: #555;
}

.detail-value {
    margin-right: 10px;
    font-size: 16px;
}

.detail-change {
    font-size: 14px;
}

.detail-change.positive {
    color: #4CAF50;
}

.detail-change.negative {
    color: #F44336;
}

.total-cost {
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px dashed #ddd;
}

.total-cost .detail-value {
    font-size: 20px;
    font-weight: bold;
    color: #d32f2f;
}

.bill-breakdown {
    flex: 1;
    min-width: 200px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding-left: 20px;
    border-left: 1px solid #eee;
}

.breakdown-item {
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 5px;
}

.breakdown-title {
    font-size: 14px;
    color: #666;
    margin-bottom: 5px;
}

.breakdown-value {
    font-size: 16px;
    font-weight: bold;
    color: #333;
}

.bill-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
}

.bill-date {
    font-size: 14px;
    color: #666;
}

.download-btn {
    background-color: #1976D2;
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s;
}

.download-btn:hover {
    background-color: #1565C0;
}

.no-bills {
    text-align: center;
    padding: 40px;
    background-color: white;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .bill-content {
        flex-direction: column;
    }

    .bill-breakdown {
        border-left: none;
        border-top: 1px solid #eee;
        padding-left: 0;
        padding-top: 15px;
    }

    .bill-footer {
        flex-direction: column;
        gap: 10px;
        align-items: flex-start;
    }

    .filter-toolbar {
        flex-direction: column;
        gap: 10px;
    }
}
</style>
