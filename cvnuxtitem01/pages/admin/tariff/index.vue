<template>
    <NuxtLayout>
        <div class="tariff-page">
            <div class="page-header">
                <h1>价格配置</h1>
                <div class="actions">
                    <el-button type="primary" @click="saveCurrentTariff" :disabled="!isFormChanged">保存更改</el-button>
                    <el-button @click="resetForm">重置</el-button>
                </div>
            </div>

            <el-card class="current-version" shadow="hover">
                <div class="version-info">
                    <span class="label">当前版本:</span>
                    <el-tag type="primary">{{ currentVersion }}</el-tag>
                    <span class="date">生效时间: {{ formatDate(currentEffectiveDate) }}</span>
                </div>
            </el-card>

            <div class="tariff-container">
                <el-card class="form-section" shadow="never">
                    <TariffLadderForm
                        :ladders="ladders"
                        @update:ladders="updateLadders"
                        @add-ladder="addLadder"
                        @remove-ladder="removeLadder"
                    />
                </el-card>

                <el-card class="history-section" shadow="never">
                    <template #header>
                        <div class="card-header">
                            <span>历史版本</span>
                        </div>
                    </template>
                    <TariffHistoryList
                        :history="tariffHistory"
                        :currentVersionId="currentVersionId"
                        @rollback="rollbackToVersion"
                    />
                </el-card>
            </div>
        </div>
    </NuxtLayout>
</template>

<script setup>
import TariffLadderForm from '~/components/admin/TariffLadderForm.vue';
import TariffHistoryList from '~/components/admin/TariffHistoryList.vue';
import { ElMessage, ElMessageBox } from 'element-plus';

definePageMeta({
    layout: 'admin'
});
// 表示阶梯价格的状态
const ladders = ref([
  { id: 1, min: 0, max: 100, price: 0.50 },
  { id: 2, min: 100, max: 300, price: 0.75 },
  { id: 3, min: 300, max: null, price: 1.00 }
]);

// 原始阶梯价格（用于检测变化和重置）
const originalLadders = ref([]);

// 阶梯价格历史版本
const tariffHistory = ref([
  { id: 'v1.0.3', version: 'v1.0.3', effectiveDate: '2023-03-15', createdBy: 'admin', status: 'current' },
  { id: 'v1.0.2', version: 'v1.0.2', effectiveDate: '2023-01-10', createdBy: 'admin', status: 'archived' },
  { id: 'v1.0.1', version: 'v1.0.1', effectiveDate: '2022-09-05', createdBy: 'system', status: 'archived' },
]);

// 当前版本信息
const currentVersionId = ref('v1.0.3');
const currentVersion = ref('v1.0.3');
const currentEffectiveDate = ref('2023-03-15');

// 计算属性：表单是否已更改
const isFormChanged = computed(() => {
  if (originalLadders.value.length !== ladders.value.length) return true;

  return JSON.stringify(ladders.value) !== JSON.stringify(originalLadders.value);
});

// 更新阶梯价格
function updateLadders(newLadders) {
  ladders.value = newLadders;
}

// 添加阶梯
function addLadder() {
  const lastLadder = ladders.value[ladders.value.length - 1];
  const newId = lastLadder.id + 1;
  const newMin = lastLadder.max || 0;

  ladders.value.push({
    id: newId,
    min: newMin,
    max: null,
    price: lastLadder.price
  });

  // 更新前一个阶梯的max值
  if (lastLadder.max === null) {
    lastLadder.max = newMin;
  }
}

// 移除阶梯
function removeLadder(id) {
  const index = ladders.value.findIndex(ladder => ladder.id === id);
  if (index === -1) return;

  // 如果删除的不是最后一个阶梯，需要调整下一个阶梯的min值
  if (index < ladders.value.length - 1) {
    ladders.value[index + 1].min = ladders.value[index].min;
  }

  ladders.value.splice(index, 1);
}

// 保存当前阶梯价格配置
function saveCurrentTariff() {
  // 生成新版本号
  const versionParts = currentVersion.value.split('.');
  const newPatchVersion = parseInt(versionParts[2]) + 1;
  const newVersion = `${versionParts[0]}.${versionParts[1]}.${newPatchVersion}`;

  // 创建新的历史记录
  const now = new Date();
  const newHistoryItem = {
    id: newVersion,
    version: newVersion,
    effectiveDate: now.toISOString().split('T')[0],
    createdBy: 'admin',
    status: 'current'
  };

  // 更新当前版本状态为归档
  const currentVersionIndex = tariffHistory.value.findIndex(item => item.id === currentVersionId.value);
  if (currentVersionIndex !== -1) {
    tariffHistory.value[currentVersionIndex].status = 'archived';
  }

  // 添加新版本到历史记录
  tariffHistory.value.unshift(newHistoryItem);

  // 更新当前版本信息
  currentVersionId.value = newVersion;
  currentVersion.value = newVersion;
  currentEffectiveDate.value = now.toISOString().split('T')[0];

  // 更新原始数据（用于检测变化）
  originalLadders.value = JSON.parse(JSON.stringify(ladders.value));

  // 显示成功消息
  ElMessage({
    message: '价格配置已成功保存！',
    type: 'success',
  });
}

// 重置表单
function resetForm() {
  ladders.value = JSON.parse(JSON.stringify(originalLadders.value));
  ElMessage({
    message: '表单已重置',
    type: 'info',
  });
}

// 回滚到指定版本
function rollbackToVersion(versionId) {
  // 实际应用中，这里应该从API获取该版本的配置
  // 这里模拟不同版本的配置
  const mockVersionData = {
    'v1.0.2': [
      { id: 1, min: 0, max: 150, price: 0.45 },
      { id: 2, min: 150, max: null, price: 0.85 }
    ],
    'v1.0.1': [
      { id: 1, min: 0, max: 200, price: 0.40 },
      { id: 2, min: 200, max: 500, price: 0.60 },
      { id: 3, min: 500, max: null, price: 0.80 }
    ]
  };

  if (mockVersionData[versionId]) {
    ElMessageBox.confirm(
      `确定要回滚到版本 ${versionId} 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
      .then(() => {
        ladders.value = JSON.parse(JSON.stringify(mockVersionData[versionId]));
        ElMessage({
          type: 'success',
          message: `已加载版本 ${versionId} 的配置，请保存以应用更改。`,
        });
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '已取消回滚操作',
        });
      });
  }
}

// 格式化日期
function formatDate(dateString) {
  const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
  return new Date(dateString).toLocaleDateString('zh-CN', options);
}

// 页面加载时初始化
onMounted(() => {
  // 实际应用中，这里应该从API获取当前配置
  originalLadders.value = JSON.parse(JSON.stringify(ladders.value));
});
</script>

<style scoped>
.tariff-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 12px;
}

.current-version {
  margin-bottom: 24px;
}

.version-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.label {
  font-weight: bold;
  color: #606266;
}

.date {
  color: #909399;
  margin-left: auto;
}

.tariff-container {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.form-section {
  flex: 3;
}

.history-section {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .tariff-container {
    flex-direction: column;
  }
}
</style>
