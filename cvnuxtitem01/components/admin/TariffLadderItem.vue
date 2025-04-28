<template>
  <div class="ladder-item" :class="{ 'first': isFirst, 'last': isLast }">
    <div class="ladder-col col-range">
      <div class="range-inputs">
        <div class="input-group">
          <input
            type="number"
            :value="ladder.min"
            @input="updateMin"
            :disabled="isFirst"
            min="0"
            step="1"
          >
          <span class="unit">度</span>
        </div>
        <span class="separator">~</span>
        <div class="input-group">
          <input
            type="number"
            :value="ladder.max"
            @input="updateMax"
            :disabled="isLast"
            :placeholder="isLast ? '无上限' : ''"
            min="0"
            step="1"
          >
          <span class="unit">度</span>
        </div>
      </div>
    </div>
    <div class="ladder-col col-price">
      <div class="input-group">
        <input
          type="number"
          :value="ladder.price"
          @input="updatePrice"
          min="0"
          step="0.01"
        >
        <span class="unit">元/度</span>
      </div>
    </div>
    <div class="ladder-col col-action">
      <button
        class="remove-btn"
        @click="$emit('remove')"
        :disabled="totalLadders <= 1"
        title="删除此阶梯"
      >
        <span class="icon">×</span>
      </button>
    </div>
  </div>
</template>

<script setup>

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

// 更新最小值
function updateMin(event) {
  const min = parseFloat(event.target.value);
  if (isNaN(min) || min < 0) return;

  // 确保min小于max
  if (props.ladder.max !== null && min >= props.ladder.max) return;

  emit('update:ladder', { ...props.ladder, min });
}

// 更新最大值
function updateMax(event) {
  const inputValue = event.target.value;
  let max = inputValue === '' ? null : parseFloat(inputValue);

  // 验证最大值
  if (max !== null) {
    if (isNaN(max) || max <= props.ladder.min) return;
  }

  emit('update:ladder', { ...props.ladder, max });
}

// 更新价格
function updatePrice(event) {
  const price = parseFloat(event.target.value);
  if (isNaN(price) || price < 0) return;

  // 保留两位小数
  const formattedPrice = Math.round(price * 100) / 100;

  emit('update:ladder', { ...props.ladder, price: formattedPrice });
}
</script>

<style scoped>
.ladder-item {
  display: flex;
  padding: 12px 16px;
  background-color: white;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  margin-bottom: 8px;
  transition: all 0.3s;
}

.ladder-item:hover {
  border-color: #d9d9d9;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.first {
  border-left: 3px solid #52c41a;
}

.last {
  border-left: 3px solid #1890ff;
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
  width: 80px;
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

.input-group input {
  width: 100%;
  padding: 8px 30px 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  transition: all 0.3s;
}

.input-group input:focus {
  border-color: #40a9ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.input-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
  color: #888;
}

.unit {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 12px;
}

.separator {
  margin: 0 12px;
  color: #999;
}

.remove-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #f0f0f0;
  background-color: white;
  color: #ff4d4f;
  cursor: pointer;
  transition: all 0.3s;
}

.remove-btn:hover:not(:disabled) {
  background-color: #fff1f0;
  border-color: #ff4d4f;
}

.remove-btn:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.icon {
  font-size: 16px;
}
</style>
