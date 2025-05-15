<script setup lang="ts">
import TariffLadderForm from '~/components/admin/TariffLadderForm.vue';
import TariffHistoryList from '~/components/admin/TariffHistoryList.vue';
import { useTariffService } from "~/services/admin/tariff.js";
import {groupByTypeAudVersion, type GroupedTariff, type TariffItem, type Version, type TariffVersion} from "~/types/admin/tariff/type";
import isEqual from 'lodash/isEqual'
import cloneDeep from 'lodash/cloneDeep'

definePageMeta({
    layout: 'admin'
});

// 新增：版本添加对话框相关状态
const showAddVersionDialog = ref(false);
const versionFormLoading = ref(false);
const versionFormRules = {
    version: [
        { required: true, message: '请输入版本号', trigger: 'blur' },
        { pattern: /^v\d+\.\d+(\.\d+)?$/, message: '版本号格式应为 v1.0 或 v1.0.1', trigger: 'blur' }
    ],
    startTime: [
        { required: true, message: '请选择生效时间', trigger: 'change' }
    ]
};
const versionFormRef = ref();

const groupData = ref<GroupedTariff>({});
const nowVersionDetails = ref<Version>();
const nowVersion = computed<string>(() => {
    return nowVersionDetails.value?.version || '';
});
const selectedTariffType = ref<TARIFF_TYPES>(TARIFF_TYPES.Water); // 当前选择的价格类型
const newVersionForm = ref<TariffVersion>({
    id: -1,
    type: selectedTariffType.value,
    version: '',
    startTime: '',
    endTime: '',
    isActive: false
})
const historyVersion = ref<Version[]>([]); // 历史版本

const getLadders = (type: TARIFF_TYPES): TariffItem[] => {
    if (!groupData.value || !groupData.value[type] || !nowVersion.value) {
        return [];
    }
    return groupData.value[type][nowVersion.value] ?? [];
};

const ladders = computed(() => getLadders(selectedTariffType.value)); // 根据类型获取数据
const currentVersionId = computed(() => {
    return nowVersionDetails.value?.id ?? -1;
});// 当前版本ID
const originalLadders = ref<TariffItem[]>([]); // 修改前的数据快照
const deletedIds = ref<number[]>([])
const isDirty = computed(() => !isEqual(ladders.value, originalLadders.value)) // 是否有未保存的脏数据


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
            originalLadders.value = cloneDeep(ladders.value)
        });
};

// 处理类型切换
function handleTypeChange(newType: TARIFF_TYPES): void {
    if (selectedTariffType.value === newType) {
        return;
    }

    if (!isDirty.value) {
        // 没有脏数据，直接切换
        selectedTariffType.value = newType;
        fetchTariffData();
        return;
    }

    // 有脏数据，显示确认对话框
    ElMessageBox.confirm('切换类型会丢失未保存的修改，确定继续吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
    })
        .then(() => {
            selectedTariffType.value = newType;
            fetchTariffData();
            resetForm();
        })
}

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

// 回滚到指定版本
function rollbackToVersion(versionId: number): void {
    if (!isDirty.value) {
        const newVersion = historyVersion.value.find(data => {
            return data.id === versionId
        })?.version;
        // console.log(newVersion);
        ElMessageBox.confirm(
            `确定要回滚到版本 ${newVersion} 吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
            .then(() => {
                useTariffService().apiTariffChangeVersion({
                    type: selectedTariffType.value,
                    oldId: currentVersionId.value,
                    newId: versionId
                }).then(()=>{
                    fetchTariffData()
                    ElMessage({
                        type: 'success',
                        message: `已加载版本 ${newVersion} 的配置，请保存以应用更改。`,
                    });
                })
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消回滚操作',
                });
            });
        return;
    }

    // 有脏数据，显示确认对话框
    ElMessageBox.confirm('回滚版本会丢失未保存的修改，确定继续吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
    })
        .then(() => {
            const newVersion = historyVersion.value.find(data => {
                return data.id === versionId
            })?.version;
            // console.log(newVersion);
            ElMessageBox.confirm(
                `确定要回滚到版本 ${newVersion} 吗？`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            )
                .then(() => {
                    useTariffService().apiTariffChangeVersion({
                        type: selectedTariffType.value,
                        oldId: currentVersionId.value,
                        newId: versionId
                    }).then(()=>{
                        fetchTariffData()
                        ElMessage({
                            type: 'success',
                            message: `已加载版本 ${newVersion} 的配置，请保存以应用更改。`,
                        });
                    })
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: '已取消回滚操作',
                    });
                });
        })

}

// 保存当前阶梯价格配置
function saveCurrentTariff(): void {
    // 验证阶梯价格的合理性
    if (!validateLadders()) {
        return;
    }

    useTariffService().apiTariffSave(selectedTariffType.value, currentVersionId.value, {
        deletedIds: deletedIds.value,
        tariffTiers: ladders.value
    })
        .then(() => {
            ElMessage({
                message: `${selectedTariffType.value === TARIFF_TYPES.Electricity ? '电价' : '水价'}配置已成功保存！`,
                type: 'success',
            });

            fetchTariffData();
        })
}

// 验证阶梯价格合理性
function validateLadders(): boolean {
    // 验证阶梯价格是否递增或相等
    for (let i = 0; i < ladders.value.length - 1; i++) {
        const currentLadder = ladders.value[i];
        const nextLadder = ladders.value[i + 1];

        // 检查当前阶梯的价格是否高于下一阶梯的价格
        // 阶梯价格应该递增，即后面阶梯价格应高于或等于前面阶梯
        if (currentLadder.price > nextLadder.price) {
            ElMessage({
                message: `错误：阶梯${i + 1}的价格(${currentLadder.price}元)高于阶梯${i + 2}的价格(${nextLadder.price}元)，违反了阶梯价格规则`,
                type: 'error',
            });
            return false;
        }
    }

    // 验证每个阶梯的上限是否大于下限
    for (let i = 0; i < ladders.value.length - 1; i++) {
        const ladder = ladders.value[i];
        const lowerBound = i === 0 ? 0 : ladders.value[i-1].upperBound;

        // 如果当前阶梯不是最后一个，且upperBound为null，则是错误的
        if (i < ladders.value.length - 1 && ladder.upperBound === null) {
            ElMessage({
                message: `错误：阶梯${i + 1}必须设置上限值`,
                type: 'error',
            });
            return false;
        }

        // 确保上限大于下限
        if (ladder.upperBound !== null && lowerBound !== null && ladder.upperBound <= lowerBound) {
            ElMessage({
                message: `错误：阶梯${i + 1}的上限值必须大于下限值`,
                type: 'error',
            });
            return false;
        }
    }

    return true;
}

// 添加阶梯
function addLadder(): void {
    const newLadders = [...ladders.value];
    const lastLadder = newLadders.length > 0 ? newLadders[newLadders.length - 1] : null;

    // 如果是第一个阶梯或者最后阶梯的上限是null
    if (newLadders.length === 0) {
        // 第一个阶梯
        newLadders.push({
            id: -1,
            seq: 1,
            upperBound: 10,
            price: 2.5,
            tariffVersion: {
                id: currentVersionId.value,
                type: selectedTariffType.value,
                version: nowVersion.value,
                startTime: nowVersionDetails.value?.startTime || '',
                endTime: '9999-12-31',
                isActive: true
            }
        });
    } else if (lastLadder) {
        // 如果最后一个阶梯上限不是null，设置一个新的上限
        let newUpperBound = null;
        if (lastLadder.upperBound !== null) {
            newUpperBound = lastLadder.upperBound + 20;
        }

        // 更新原来的最后一个阶梯，确保它有上限
        if (lastLadder.upperBound === null) {
            const lowerBound = newLadders.length === 1 ? 0 :
                (newLadders[newLadders.length - 2]?.upperBound ?? 0);
            lastLadder.upperBound = lowerBound + 20;
        }

        // 添加新阶梯
        newLadders.push({
            id: -1,
            upperBound: newUpperBound,
            price: lastLadder.price + 1,
            seq: lastLadder.seq + 1,
            tariffVersion: {
                id: currentVersionId.value,
                type: selectedTariffType.value,
                version: nowVersion.value,
                startTime: nowVersionDetails.value?.startTime || '',
                endTime: '9999-12-31',
                isActive: true
            }
        });
    }

    // 更新阶梯数据
    if (groupData.value && selectedTariffType.value && nowVersion.value) {
        (groupData.value[selectedTariffType.value] ??= {})[nowVersion.value] = newLadders;
    }
}

// 移除阶梯
function removeLadder(id: number): void {
    const newLadders = [...ladders.value];
    const index = newLadders.findIndex(ladder => ladder.id === id);
    const deletedId: number = newLadders[index]?.id;
    // console.log(index)
    if (index === -1) return;

    // 删除该阶梯
    newLadders.splice(index, 1);

    // 添加到删除列表
    if (deletedId !== -1) deletedIds.value.push(deletedId)
    console.log(deletedIds.value)

    // 如果删除后没有阶梯了，添加一个默认阶梯
    if (newLadders.length === 0) {
        addLadder();
        return;
    }

    // 如果删除的是最后一个阶梯，需要将前一个阶梯的上限设为null
    if (index === newLadders.length && newLadders.length > 0) {
        const lastIndex = newLadders.length - 1;
        if (lastIndex >= 0 && newLadders[lastIndex]) {
            newLadders[lastIndex].upperBound = null;
        }
    }

    // 更新阶梯序号
    for (let i = 0; i < newLadders.length; i++) {
        if (newLadders[i]) {
            newLadders[i].seq = i + 1;
        }
    }

    // 更新阶梯数据
    if (groupData.value && selectedTariffType.value && nowVersion.value) {
        (groupData.value[selectedTariffType.value] ??= {})[nowVersion.value] = newLadders;
    }
}

// 重置表单
function resetForm(): void {
    if (groupData.value && selectedTariffType.value && nowVersion.value) {
        (groupData.value[selectedTariffType.value] ??= {})[nowVersion.value] = JSON.parse(JSON.stringify(originalLadders.value));
    }
    deletedIds.value = [];

    ElMessage({
        message: '表单已重置',
        type: 'info',
    });
}

// 表单是否已更改
const isFormChanged = computed<boolean>(() => {
    if (originalLadders.value.length !== ladders.value.length) return true;
    return JSON.stringify(ladders.value) !== JSON.stringify(originalLadders.value);
});

// 打开添加版本对话框
const openAddVersionDialog = (): void => {
    showAddVersionDialog.value = true;
    newVersionForm.value = {
        id: -1,
        type: selectedTariffType.value,
        version: '',
        startTime: '',
        endTime: '9999-12-31',
        isActive: false
    };
}

// 提交新版本
const submitNewVersion = async () => {
    if (!versionFormRef.value) return;

    await versionFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            versionFormLoading.value = true;
            useTariffService()
                .apiTariffAddVersion(newVersionForm.value)
                .then(_ => {
                    ElMessage({
                        type: 'success',
                        message: '新版本创建成功'
                    });

                    showAddVersionDialog.value = false;
                    fetchTariffData();
                })
                .finally(()=>{
                    versionFormLoading.value = false;
                })
        }
    });
};

onMounted(() => {
    fetchTariffData();
});
onBeforeRouteLeave((_to, _from, next) => {
    if (!isDirty.value) return next()

    ElMessageBox.confirm('您有未保存的修改，确定要离开吗？', '提示', {
        type: 'warning',
        confirmButtonText: '仍然离开',
        cancelButtonText: '取消'
    })
        .then(() => next())
        .catch(() => next(false))
    })
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
                        <el-radio-button :label="TARIFF_TYPES.Water" @click.native.prevent="handleTypeChange(TARIFF_TYPES.Water)">水价设置</el-radio-button>
                        <el-radio-button :label="TARIFF_TYPES.Electricity" @click.native.prevent="handleTypeChange(TARIFF_TYPES.Electricity)">电价设置</el-radio-button>
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
                        @add-ladder="addLadder"
                        @remove-ladder="removeLadder"
                        :unit="currentUnit"
                    />
                </el-card>

                <el-card class="history-section" shadow="never">
                    <template #header>
                        <div class="card-header">
                            <span>历史版本</span>
                            <div>
                                <el-button circle type="primary" @click="openAddVersionDialog">
                                    <el-icon><ElIconPlus/></el-icon>
                                </el-button>
                            </div>
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

        <el-dialog
            v-model="showAddVersionDialog"
            title="添加新版本"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form
                ref="versionFormRef"
                :model="newVersionForm"
                :rules="versionFormRules"
                label-width="80px"
                label-position="right"
            >
                <el-form-item label="版本号" prop="version">
                    <el-input
                        v-model="newVersionForm.version"
                        placeholder="请输入版本号，如 v1.0"
                    />
                </el-form-item>
                <el-form-item label="开始时间" prop="startTime">
                    <el-date-picker
                        v-model="newVersionForm.startTime"
                        type="date"
                        placeholder="选择生效时间"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="结束时间">
                    <el-date-picker
                        v-model="newVersionForm.endTime"
                        type="date"
                        placeholder="选择生效时间"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showAddVersionDialog = false">取消</el-button>
                <el-button
                    type="primary"
                    @click="submitNewVersion"
                    :loading="versionFormLoading"
                >
                    创建
                </el-button>
            </template>
        </el-dialog>
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
