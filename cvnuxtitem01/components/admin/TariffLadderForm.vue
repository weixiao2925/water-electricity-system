<script setup lang="ts">
import type { TariffItem } from '~/types/admin/tariff/type';

const props = defineProps({
    ladders: {
      type: Array as () => TariffItem[],
      required: true
    },
    unit: {
      type: String,
      default: '度'
    }
});

const emit = defineEmits<{
    'update:ladders': [ladders: TariffItem[]];
    'add-ladder': [];
    'remove-ladder': [id: number];
}>();


function updateUpperBound(index: number, value: number | undefined): void {
  const newLadders = [...props.ladders];
  newLadders[index].upperBound = value === undefined ? null : value;
  emit('update:ladders', newLadders);
}

function addLadder(): void {
    emit('add-ladder');
}

function removeLadder(id: number): void {
  ElMessageBox.confirm(
    '删除阶梯后，后续阶梯将自动前移并重新编号，范围也会相应调整。确定要删除吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    emit('remove-ladder', id);
  }).catch(() => {
    ElMessage.info("已取消")
  });
}

// 计算每个阶梯的最小值，基于上一个阶梯的upperBound
function getLowerBound(index: number): number {
  if (index === 0) return 0;

  const prevLadder = props.ladders[index - 1];
  return prevLadder?.upperBound !== null ? prevLadder.upperBound : 0;
}
</script>

<template>
    <div class="ladder-form">
      <div class="ladder-items">
        <div v-for="(ladder, index) in ladders" :key="ladder.id" class="ladder-item">
          <div class="ladder-header">
            <h3>阶梯 {{ index + 1 }}</h3>
            <el-button
              v-if="ladders.length > 1"
              size="small"
              type="danger"
              @click="removeLadder(ladder.id)"
            >
              删除
            </el-button>
          </div>

          <div class="ladder-content">
            <el-form label-position="top">
              <el-form-item label="范围">
                <div class="range-inputs">
                  <!-- 显示下限值，但不可编辑，由前一阶梯的上限决定 -->
                  <el-input-number
                    :model-value="getLowerBound(index)"
                    disabled
                    :precision="1"
                    :step="1"
                    :min="0"
                  />
                  <span class="range-separator">至</span>
                  <el-input-number
                    :model-value="ladder.upperBound === null ? undefined : ladder.upperBound"
                    v-if="index !== ladders.length - 1"
                    :precision="1"
                    :step="1"
                    :min="getLowerBound(index)"
                    @change="(value) => updateUpperBound(index, value)"
                  />
                  <span v-if="index === ladders.length - 1" class="unlimited">不限</span>
                  <span class="unit">{{ unit }}</span>
                </div>
              </el-form-item>

              <el-form-item label="价格 (元)">
                <el-input-number
                  v-model="ladder.price"
                  :precision="2"
                  :step="0.01"
                  :min="0"
                />
              </el-form-item>

            </el-form>
          </div>
        </div>
      </div>

      <div class="add-ladder">
        <el-button type="primary" @click="addLadder">添加阶梯</el-button>
      </div>
    </div>
</template>

<style scoped>
.ladder-form {
padding: 10px 0;
}

.ladder-items {
display: flex;
flex-direction: column;
gap: 30px;
}

.ladder-item {
border: 1px solid #e4e7ed;
border-radius: 4px;
padding: 15px;
background-color: #f9f9f9;
}

.ladder-header {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 15px;
border-bottom: 1px solid #e4e7ed;
padding-bottom: 10px;
}

.ladder-header h3 {
margin: 0;
font-size: 16px;
}

.ladder-content {
padding: 0 10px;
}

.range-inputs {
display: flex;
align-items: center;
gap: 10px;
}

.range-separator {
color: #606266;
}

.unlimited {
color: #909399;
margin-left: 5px;
}

.unit {
margin-left: 5px;
color: #606266;
}

.add-ladder {
margin-top: 20px;
display: flex;
justify-content: center;
}
</style>
