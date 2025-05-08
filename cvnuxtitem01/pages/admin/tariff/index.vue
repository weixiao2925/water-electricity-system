<script setup lang="ts">
import TariffLadderForm from '~/components/admin/TariffLadderForm.vue';
import TariffHistoryList from '~/components/admin/TariffHistoryList.vue';
import { useTariffService } from "~/services/admin/tariff.js";
import {groupByTypeAudVersion, type GroupedTariff, type TariffItem, type Version} from "~/types/admin/tariff/type";


definePageMeta({
    layout: 'admin'
});


const groupData = ref<GroupedTariff>({});
const nowVersionDetails = ref<Version>();
const nowVersion = computed<string>(() => {
    return nowVersionDetails.value?.version || '';
});
const selectedTariffType = ref<TARIFF_TYPES>(TARIFF_TYPES.Electricity); // 当前选择的价格类型
const historyVersion = ref<Version[]>([]); // 历史版本

// 简化一个访问辅助函数，避免重复访问逻辑
const getLadders = (type: TARIFF_TYPES): TariffItem[] => {
    return groupData.value?.[type]?.[nowVersion.value] ?? [];
};

// 所有 computed 都能简化成调用这个：
const ladders = computed(() => getLadders(selectedTariffType.value));
const currentVersionId = computed(() => {
    return nowVersionDetails.value?.id ?? -1;
});// 当前版本ID
const originalLadders = ref<TariffItem[]>([]);


// 获取价格数据和当前版本
const fetchTariffData = () => {
    const promises = [
        useTariffService().apiTariffList(),
        useTariffService().apiTariffNowVersion(selectedTariffType.value),
        useTariffService().apiTariffVersion(selectedTariffType.value)
    ]
    Promise.all(promises)
        .then(response => {
            groupData.value = groupByTypeAudVersion(response[0].data);
            nowVersionDetails.value = response[1].data;
            historyVersion.value = response[2].data;
            // 保存原始数据用于后续比较
            originalLadders.value = JSON.parse(JSON.stringify(ladders.value));

            // console.log(selectedTariffType.value)
            // console.log(response);
            // console.log(response[2]);
        });
};

// 当前版本信息
const currentVersion = computed<string>(() => {
    return nowVersion.value;
});

// 根据当前类型获取单位
const currentUnit = computed<string>(() => {
    return selectedTariffType.value === TARIFF_TYPES.Electricity ? '度' : '吨';
});

// 格式化日期
function formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return new Date(dateString).toLocaleDateString('zh-CN', options);
}


watch(selectedTariffType, () => {
    fetchTariffData();
});// 当切换类型时重新获取数据

onMounted(() => {
    fetchTariffData();
});


// 计算属性：表单是否已更改
const isFormChanged = computed<boolean>(() => {
    if (originalLadders.value.length !== ladders.value.length) return true;
    return JSON.stringify(ladders.value) !== JSON.stringify(originalLadders.value);
});

// 更新阶梯价格
function updateLadders(newLadders: TariffItem[]): void {
    // 这里可以实现实际的更新逻辑
    console.log("更新阶梯", newLadders);
}

// 添加阶梯
function addLadder(): void {
    // // 实现添加阶梯的逻辑
    // const lastLadder = ladders.value[ladders.value.length - 1];
    // const newId = lastLadder.id + 1;
    // const newMin = lastLadder.max || 0;
    //
    // ladders.value.push({
    //     id: newId,
    //     min: newMin,
    //     max: null,
    //     price: lastLadder.price
    // });
    //
    // // 更新前一个阶梯的max值
    // if (lastLadder.max === null) {
    //     lastLadder.max = newMin;
    // }
}

// 移除阶梯
function removeLadder(id: number): void {
    // const index = ladders.value.findIndex(ladder => ladder.id === id);
    // if (index === -1) return;
    //
    // // 如果删除的不是最后一个阶梯，需要调整下一个阶梯的min值
    // if (index < ladders.value.length - 1) {
    //     ladders.value[index + 1].min = ladders.value[index].min;
    // }
    //
    // ladders.value.splice(index, 1);
}

// 保存当前阶梯价格配置
function saveCurrentTariff(): void {
    // useTariffService().apiTariffSave({
    //     type: selectedTariffType.value,
    //     ladders: ladders.value
    // }).then(() => {
    //     ElMessage({
    //         message: `${selectedTariffType.value === TARIFF_TYPES.Electricity ? '电价' : '水价'}配置已成功保存！`,
    //         type: 'success',
    //     });
    //
    //     // 重新获取数据以更新版本信息
    //     fetchTariffData();
    //     fetchTariffHistory();
    // }).catch(error => {
    //     ElMessage({
    //         message: `保存失败: ${error.message || '未知错误'}`,
    //         type: 'error',
    //     });
    // });
}

// 重置表单
function resetForm(): void {
    // ladders.value = JSON.parse(JSON.stringify(originalLadders.value));
    //
    // ElMessage({
    //     message: '表单已重置',
    //     type: 'info',
    // });
}

// 回滚到指定版本
function rollbackToVersion(versionId: string): void {
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
        // useTariffService().apiTariffVersionData({
        //     type: selectedTariffType.value,
        //     version: versionId
        // }).then(response => {
        //     // 假设API返回了指定版本的配置数据
        //     ladders.value = response.data;
        //
        //     ElMessage({
        //         type: 'success',
        //         message: `已加载版本 ${versionId} 的配置，请保存以应用更改。`,
        //     });
        // }).catch(error => {
        //     ElMessage({
        //         type: 'error',
        //         message: `获取版本数据失败: ${error.message || '未知错误'}`,
        //     });
        // });
    })
    .catch(() => {
        ElMessage({
            type: 'info',
            message: '已取消回滚操作',
        });
    });
}

</script>

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

            <el-card class="tariff-type-selector" shadow="hover">
                <div class="type-selector">
                    <el-radio-group v-model="selectedTariffType" size="large">
                        <el-radio-button :label="TARIFF_TYPES.Electricity">电价设置</el-radio-button>
                        <el-radio-button :label="TARIFF_TYPES.Water">水价设置</el-radio-button>
                    </el-radio-group>
                </div>
            </el-card>

            <el-card class="current-version" shadow="hover">
                <div class="version-info">
                    <span class="label">当前版本:</span>
                    <el-tag type="primary">{{ currentVersion }}</el-tag>
                    <span class="date">
                        生效时间: {{ formatDate(nowVersionDetails?.startTime || '') ?? '无数据' }}
                    </span>
                </div>
            </el-card>

            <div class="tariff-container">
                <el-card class="form-section" shadow="never">
                    <template #header>
                        <div class="card-header">
                            <span>{{ selectedTariffType === TARIFF_TYPES.Electricity ? '电价' : '水价' }}阶梯设置</span>
                            <el-tag type="info">单位: {{ selectedTariffType === TARIFF_TYPES.Electricity ? '元/度' : '元/吨' }}</el-tag>
                        </div>
                    </template>
                    <TariffLadderForm
                        :ladders="ladders"
                        @update:ladders="updateLadders"
                        @add-ladder="addLadder"
                        @remove-ladder="removeLadder"
                        :unit="currentUnit"
                    />
                </el-card>

                <el-card class="history-section" shadow="never">
                    <template #header>
                        <div class="card-header">
                            <span>历史版本</span>
                        </div>
                    </template>
                    <TariffHistoryList
                        :history="historyVersion"
                        :currentVersionId="currentVersionId"
                        @rollback="rollbackToVersion"
                    />
                </el-card>
            </div>
        </div>
    </NuxtLayout>
</template>

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

.tariff-type-selector {
  margin-bottom: 24px;
}

.type-selector {
  display: flex;
  justify-content: center;
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
