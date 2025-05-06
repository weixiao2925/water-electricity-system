<template>
  <el-card
    class="ladder-item"
    :class="{ 'first': isFirst, 'last': isLast }"
    :body-style="{ padding: '12px', display: 'flex' }"
    shadow="hover"
  >
    <div class="ladder-col col-range">
      <div class="range-inputs">
        <div class="input-group">
          <el-input-number
            v-model="ladderData.min"
            :disabled="isFirst"
            :min="0"
            :step="1"
            :precision="0"
            @change="updateMin"
            size="default"
          />
          <span class="unit">度</span>
        </div>
        <span class="separator">~</span>
        <div class="input-group">
          <el-input-number
            v-model="ladderData.max"
            :disabled="isLast"
            :min="0"
            :step="1"
            :precision="0"
            :placeholder="isLast ? '无上限' : ''"
            @change="updateMax"
            size="default"
          />
          <span class="unit">度</span>
        </div>
      </div>
    </div>
    <div class="ladder-col col-price">
      <div class="input-group">
        <el-input-number
          v-model="ladderData.price"
          :min="0"
          :step="0.01"
          :precision="2"
          @change="updatePrice"
          size="default"
        />
        <span class="unit">元/度</span>
      </div>
    </div>
    <div class="ladder-col col-action">
      <el-tooltip
        content="删除此阶梯"
        placement="top"
        :disabled="totalLadders <= 1"
      >
        <el-button
          type="danger"
          :icon="Delete"
          circle
          @click="$emit('remove')"
          :disabled="totalLadders <= 1"
          size="small"
        />
      </el-tooltip>
    </div>
  </el-card>
</template>

<script setup>
import { reactive, watch } from 'vue';
import { Delete } from '@element-plus/icons-vue';

const props = defineProps({
  ladder: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  },
  isFirst: {
    type: Boolean,
    default: false
  },
  isLast: {
    type: Boolean,
    default: false
  },
  totalLadders: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['update:ladder', 'remove']);

// 使用响应式数据更好地处理组件内部状态
const ladderData = reactive({
  min: props.ladder.min,
  max: props.ladder.max,
  price: props.ladder.price
});

// 监听外部传入的ladder变化
watch(() => props.ladder, (newVal) => {
  ladderData.min = newVal.min;
  ladderData.max = newVal.max;
  ladderData.price = newVal.price;
}, { deep: true });

// 更新最小值
function updateMin(value) {
  if (isNaN(value) || value < 0) return;

  // 确保min小于max
  if (ladderData.max !== null && value >= ladderData.max) return;

  emit('update:ladder', { ...props.ladder, min: value });
}

// 更新最大值
function updateMax(value) {
  if (value !== null && value <= ladderData.min) return;

  emit('update:ladder', { ...props.ladder, max: value });
}

// 更新价格
function updatePrice(value) {
  if (isNaN(value) || value < 0) return;
  emit('update:ladder', { ...props.ladder, price: value });
}
</script>

<style scoped>
.ladder-item {
  margin-bottom: 8px;
}

.ladder-item :deep(.el-card__body) {
  width: 100%;
}

.first {
  border-left: 3px solid #67c23a;
}

.last {
  border-left: 3px solid #409eff;
}

.ladder-col {
  flex: 1;
  display: flex;
  align-items: center;
}

.col-range {
  flex: 2;
}

.col-price {
  flex: 1;
}

.col-action {
  width: 60px;
  display: flex;
  justify-content: center;
}

.range-inputs {
  display: flex;
  align-items: center;
  width: 100%;
}

.input-group {
  position: relative;
  flex: 1;
}

.unit {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 12px;
  z-index: 1;
}

.separator {
  margin: 0 12px;
  color: #999;
}
</style>
