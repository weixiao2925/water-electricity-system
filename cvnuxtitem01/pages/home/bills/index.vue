<script setup lang="ts">
// 模拟账单数据
import {Download, Wallet} from "@element-plus/icons-vue";
import { ref, computed } from 'vue';

// 定义支付方式类型
type PaymentMethod = 'alipay' | 'wechat' | 'unionpay' | 'creditcard';

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
        isPaid: false,
        paidDate: ''
    },
    {
        id: 5,
        month: '2023-01',
        water: { reading: 107.2, cost: 67.3, lastReading: 104.0, usage: 3.2 },
        electricity: { reading: 512.5, cost: 176.8, lastReading: 500.1, usage: 12.4 },
        gas: { reading: 81.0, cost: 152.2, lastReading: 78.0, usage: 3.0 },
        totalCost: 396.3,
        status: '已出账',
        isPaid: false,
        paidDate: ''
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

// 支付相关变量和方法
const paymentDialogVisible = ref(false);
const currentBill = ref<any>(null);
const selectedPaymentMethod = ref<PaymentMethod>('alipay');
const paymentLoading = ref(false);
const paymentQRCode = ref('');
const paymentStep = ref(1); // 1: 选择支付方式 2: 显示支付二维码 3: 支付结果

const paymentMethods = [
    { value: 'alipay', label: '支付宝', icon: 'https://zos.alipayobjects.com/rmsportal/nOUVKmLtktfaqBu.png' },
    { value: 'wechat', label: '微信支付', icon: 'https://res.wx.qq.com/a/wx_fed/assets/res/OTE0YTAw.png' },
    { value: 'unionpay', label: '银联云闪付', icon: 'https://cn.unionpay.com/upowhtml/cn/resources/images/header/homepage-logo.png' },
    { value: 'creditcard', label: '信用卡', icon: 'https://img.icons8.com/color/48/000000/visa.png' },
];

// 卡号相关
const cardNumber = ref('');
const cardName = ref('');
const cardExpiry = ref('');
const cardCVV = ref('');

// 信用卡付款表单校验
const validateCardNumber = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入卡号'));
    } else if (!/^\d{16}$/.test(value.replace(/\s/g, ''))) {
        callback(new Error('卡号格式不正确'));
    } else {
        callback();
    }
};

const validateCardExpiry = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入有效期'));
    } else if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(value)) {
        callback(new Error('有效期格式不正确 (MM/YY)'));
    } else {
        callback();
    }
};

const validateCardCVV = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入安全码'));
    } else if (!/^\d{3,4}$/.test(value)) {
        callback(new Error('安全码格式不正确'));
    } else {
        callback();
    }
};

const cardFormRules = {
    cardNumber: [{ validator: validateCardNumber, trigger: 'blur' }],
    cardName: [{ required: true, message: '请输入持卡人姓名', trigger: 'blur' }],
    cardExpiry: [{ validator: validateCardExpiry, trigger: 'blur' }],
    cardCVV: [{ validator: validateCardCVV, trigger: 'blur' }],
};

// 打开支付对话框
const openPaymentDialog = (bill: any) => {
    currentBill.value = bill;
    paymentDialogVisible.value = true;
    paymentStep.value = 1;
    selectedPaymentMethod.value = 'alipay';
    resetCardForm();
};

// 重置信用卡表单
const resetCardForm = () => {
    cardNumber.value = '';
    cardName.value = '';
    cardExpiry.value = '';
    cardCVV.value = '';
};

// 格式化卡号
const formatCardNumber = (value: string) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const matches = v.match(/\d{4,16}/g);
    const match = matches && matches[0] || '';
    const parts = [];

    for (let i = 0, len = match.length; i < len; i += 4) {
        parts.push(match.substring(i, i + 4));
    }

    if (parts.length) {
        return parts.join(' ');
    } else {
        return value;
    }
};

// 处理卡号输入
const handleCardNumberInput = () => {
    cardNumber.value = formatCardNumber(cardNumber.value);
};

// 继续到支付确认步骤
const proceedToPayment = async () => {
    if (selectedPaymentMethod.value === 'creditcard') {
        // 如果是信用卡支付，直接进行支付处理
        await processPayment();
    } else {
        // 其他支付方式显示二维码
        paymentStep.value = 2;
        paymentLoading.value = true;

        // 模拟获取支付二维码
        setTimeout(() => {
            // 这里应该是从后端获取支付二维码的URL
            // 使用占位图片代替实际二维码
            if (selectedPaymentMethod.value === 'alipay') {
                paymentQRCode.value = 'https://qr.alipay.com/bax03112wln1sy1q8fls003a'; // 这只是示例，实际应从API获取
            } else if (selectedPaymentMethod.value === 'wechat') {
                paymentQRCode.value = 'https://via.placeholder.com/200x200?text=WeChat+Payment+QR'; // 示例
            } else {
                paymentQRCode.value = 'https://via.placeholder.com/200x200?text=UnionPay+QR'; // 示例
            }
            paymentLoading.value = false;
        }, 1500);
    }
};

// 处理支付
const processPayment = async () => {
    paymentLoading.value = true;

    // 在实际应用中，这里应该调用支付API
    // 这里使用setTimeout模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000));

    // 更新账单状态
    if (currentBill.value) {
        const billIndex = bills.value.findIndex(b => b.id === currentBill.value.id);
        if (billIndex !== -1) {
            bills.value[billIndex].isPaid = true;
            bills.value[billIndex].paidDate = new Date().toISOString().split('T')[0];
        }
    }

    paymentStep.value = 3; // 显示支付结果
    paymentLoading.value = false;
};

// 检查二维码支付状态
const checkQRCodePaymentStatus = () => {
    paymentLoading.value = true;

    // 模拟检查支付状态
    setTimeout(() => {
        // 模拟支付成功
        if (currentBill.value) {
            const billIndex = bills.value.findIndex(b => b.id === currentBill.value.id);
            if (billIndex !== -1) {
                bills.value[billIndex].isPaid = true;
                bills.value[billIndex].paidDate = new Date().toISOString().split('T')[0];
            }
        }

        paymentStep.value = 3; // 显示支付结果
        paymentLoading.value = false;
    }, 1500);
};

// 关闭支付对话框
const closePaymentDialog = () => {
    paymentDialogVisible.value = false;
    currentBill.value = null;
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

                            <div class="bill-actions">
                                <!-- 添加支付按钮（仅对未支付账单显示） -->
                                <el-button
                                    v-if="!bill.isPaid"
                                    type="success"
                                    size="small"
                                    @click="openPaymentDialog(bill)"
                                    :icon="Wallet"
                                >
                                    立即支付
                                </el-button>

                                <el-button
                                    type="primary"
                                    size="small"
                                    @click="downloadPDF(bill.id)"
                                    :icon="Download"
                                >
                                    下载PDF账单
                                </el-button>
                            </div>
                        </div>
                    </el-card>
                </div>
            </el-main>
        </el-container>

        <!-- 支付对话框 -->
        <el-dialog
            v-model="paymentDialogVisible"
            :title="paymentStep === 3 ? '支付结果' : (paymentStep === 2 ? '扫码支付' : '选择支付方式')"
            width="550px"
            :close-on-click-modal="false"
            :before-close="closePaymentDialog"
        >
            <!-- 步骤指示器 -->
            <el-steps :active="paymentStep" finish-status="success" simple style="margin-bottom: 20px">
                <el-step title="选择支付方式"></el-step>
                <el-step title="确认支付"></el-step>
                <el-step title="完成支付"></el-step>
            </el-steps>

            <!-- 步骤1: 选择支付方式 -->
            <div v-if="paymentStep === 1">
                <div class="payment-info">
                    <p>账单: {{ currentBill ? getFormattedDate(currentBill.month) : '' }} 水电气费</p>
                    <p class="payment-amount">金额: <span>¥{{ currentBill ? currentBill.totalCost.toFixed(2) : '0.00' }}</span></p>
                </div>

                <el-divider>请选择支付方式</el-divider>

                <!-- 支付方式列表 -->
                <el-radio-group v-model="selectedPaymentMethod" class="payment-method-group">
                    <el-radio-button v-for="method in paymentMethods" :key="method.value" :label="method.value" class="payment-method-item">
                        <div class="payment-method-content">
                            <el-image :src="method.icon" :alt="method.label" fit="contain" class="payment-method-icon" />
<!--                            <span>{{ method.label }}</span>-->
                        </div>
                    </el-radio-button>
                </el-radio-group>

                <!-- 信用卡表单 -->
                <div v-if="selectedPaymentMethod === 'creditcard'" class="credit-card-form">
                    <el-form :rules="cardFormRules" label-position="top">
                        <el-form-item label="卡号" prop="cardNumber">
                            <el-input
                                v-model="cardNumber"
                                placeholder="1234 5678 9012 3456"
                                maxlength="19"
                                @input="handleCardNumberInput"
                            ></el-input>
                        </el-form-item>
                        <el-form-item label="持卡人姓名" prop="cardName">
                            <el-input v-model="cardName" placeholder="张三"></el-input>
                        </el-form-item>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="有效期 (MM/YY)" prop="cardExpiry">
                                    <el-input v-model="cardExpiry" placeholder="05/25"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="安全码" prop="cardCVV">
                                    <el-input v-model="cardCVV" placeholder="123" maxlength="4" show-password></el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </div>
            </div>

            <!-- 步骤2: 显示支付二维码 -->
            <div v-else-if="paymentStep === 2" class="payment-qrcode-container">
                <div v-if="paymentLoading" class="payment-loading">
                    <el-skeleton :rows="3" animated />
                </div>
                <div v-else class="qrcode-content">
                    <p>请使用{{ selectedPaymentMethod === 'alipay' ? '支付宝' : (selectedPaymentMethod === 'wechat' ? '微信' : '银联云闪付') }}扫描下方二维码进行支付</p>
                    <img :src="paymentQRCode" alt="支付二维码" class="payment-qrcode" />
                    <p class="qrcode-amount">￥{{ currentBill ? currentBill.totalCost.toFixed(2) : '0.00' }}</p>
                </div>
            </div>

            <!-- 步骤3: 显示支付结果 -->
            <div v-else-if="paymentStep === 3" class="payment-result">
                <div class="result-icon-container">
                    <el-icon class="result-icon success"><el-icon-circle-check /></el-icon>
                </div>
                <h2 class="result-title">支付成功</h2>
                <p class="result-desc">您已成功支付了 {{ currentBill ? getFormattedDate(currentBill.month) : '' }} 的水电气费账单</p>
                <p class="result-amount">支付金额: ￥{{ currentBill ? currentBill.totalCost.toFixed(2) : '0.00' }}</p>
                <p class="result-date">支付时间: {{ new Date().toLocaleString('zh-CN') }}</p>
            </div>

            <!-- 对话框底部 -->
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="closePaymentDialog">{{ paymentStep === 3 ? '完成' : '取消' }}</el-button>
                    <el-button
                        v-if="paymentStep === 1"
                        type="primary"
                        @click="proceedToPayment"
                        :loading="paymentLoading"
                    >
                        确认支付
                    </el-button>
                    <el-button
                        v-if="paymentStep === 2"
                        type="primary"
                        @click="checkQRCodePaymentStatus"
                        :loading="paymentLoading"
                    >
                        我已支付
                    </el-button>
                </span>
            </template>
        </el-dialog>
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

.bill-actions {
    display: flex;
    gap: 10px;
}

/* 支付对话框样式 */
.payment-info {
    text-align: center;
    margin-bottom: 20px;
}

.payment-amount {
    font-size: 18px;
}

.payment-amount span {
    color: #f56c6c;
    font-weight: bold;
    font-size: 24px;
}

.payment-method-group {
    display: flex;
    flex-wrap: nowrap;
    gap: 15px;
    margin-top: 20px;
    justify-content: center;
}

.payment-method-item {
    margin: 0;
    border:  1px solid #ebeef5;
}

.payment-method-content {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    gap: 10px;
}

.payment-method-icon {
    width: 60px;
    height: 40px;
}

.credit-card-form {
    margin-top: 20px;
    border-top: 1px solid #ebeef5;
    padding-top: 20px;
}

.payment-qrcode-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px 0;
}

.payment-loading {
    width: 100%;
    padding: 40px 0;
}

.qrcode-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.payment-qrcode {
    width: 200px;
    height: 200px;
    margin: 20px 0;
}

.qrcode-amount {
    font-size: 24px;
    font-weight: bold;
    color: #f56c6c;
}

.payment-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 20px 0;
}

.result-icon-container {
    margin-bottom: 20px;
}

.result-icon {
    font-size: 60px;
}

.result-icon.success {
    color: #67c23a;
}

.result-title {
    font-size: 24px;
    margin-bottom: 15px;
}

.result-desc {
    color: #606266;
    margin-bottom: 20px;
}

.result-amount {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
    margin-bottom: 10px;
}

.result-date {
    color: #909399;
    font-size: 14px;
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

    .payment-method-item {
        width: 100%;
    }
}
</style>
