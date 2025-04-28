<template>
  <div class="tariff-ladder-form">
    <div class="form-header">
      <h2>阶梯价格配置</h2>
      <button class="add-btn" @click="$emit('add-ladder')">
        <span class="icon">+</span> 添加阶梯
      </button>
    </div>
    <div class="ladder-header">
      <div class="ladder-col col-range">用量范围 (度)</div>
      <div class="ladder-col col-price">单价 (元/度)</div>
      <div class="ladder-col col-action">操作</div>
    </div>
    <div class="ladder-list">
      <TariffLadderItem
        v-for="(ladder, index) in ladders"
        :key="ladder.id"
        :ladder="ladder"
        :index="index"
        :isFirst="index === 0"
        :isLast="index === ladders.length - 1"
        :totalLadders="ladders.length"
        @update:ladder="updateLadder(index, $event)"
        @remove="$emit('remove-ladder', ladder.id)"
      />
    </div>
    <div class="form-notes">
      <p><strong>注意:</strong> 最后一个阶梯的最大值可留空，表示无上限</p>
      <p>阶梯范围必须连续，不能有空缺或重叠</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import TariffLadderItem from '@/components/admin/TariffLadderItem.vue';

const props = defineProps({
  ladders: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['update:ladders', 'add-ladder', 'remove-ladder']);

// 更新单个阶梯项
function updateLadder(index, updatedLadder) {
  const newLadders = [...props.ladders];
  newLadders[index] = updatedLadder;

  // 如果修改了某个阶梯的max值，需要更新下一个阶梯的min值
  if (index < newLadders.length - 1 && updatedLadder.max !== null) {
    newLadders[index + 1] = {
      ...newLadders[index + 1],
      min: updatedLadder.max
    };
  }

  // 如果修改了某个阶梯的min值，需要更新上一个阶梯的max值
  if (index > 0 && updatedLadder.min !== null) {
    newLadders[index - 1] = {
      ...newLadders[index - 1],
      max: updatedLadder.min
    };
  }

  emit('update:ladders', newLadders);
}
</script>

<style scoped>
.tariff-ladder-form {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.form-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.add-btn {
  display: flex;
  align-items: center;
  background-color: #f0f8ff;
  color: #1890ff;
  border: 1px dashed #1890ff;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-btn:hover {
  background-color: #e6f7ff;
}

.add-btn .icon {
  margin-right: 4px;
  font-weight: bold;
}

.ladder-header {
  display: flex;
  background-color: #fafafa;
  padding: 12px 16px;
  border-radius: 4px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.ladder-col {
  flex: 1;
}

.col-range {
  flex: 2;
}

.col-price {
  flex: 1;
}

.col-action {
  width: 80px;
  text-align: center;
}

.ladder-list {
  margin-bottom: 16px;
}

.form-notes {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #f0f0f0;
  font-size: 14px;
  color: #999;
}

.form-notes p {
  margin: 4px 0;
}
</style>
