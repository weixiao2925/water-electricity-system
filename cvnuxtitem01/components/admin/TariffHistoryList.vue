<script setup lang="ts">
import type {Version} from "~/types/admin/tariff/type";
defineProps({
    history: {
        type: Array as () => Version[],
        required: true
    },
    currentVersionId: {
        type: Number,
        required: true
    }
});
defineEmits(['rollback']);

// 使用全局的日期格式化工具函数或通过props传入
function formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return new Date(dateString).toLocaleDateString('zh-CN', options);
}
</script>

<template>
  <div class="history-list">
    <div class="list-content">
      <div
        v-for="item in history"
        :key="item.id"
        class="history-item"
        :class="{ 'current': item.id === currentVersionId }"
      >
        <div class="item-header">
          <span class="version">{{ item.version }}</span>
          <span class="status" :class="{ 'current': item.id === currentVersionId }">
            {{ item.id === currentVersionId ? '当前' : '历史' }}
          </span>
        </div>
        <div class="item-body">
          <div class="date">
            <span class="label">生效日期:</span>
            <span>{{ formatDate(item.startTime) }}</span>
          </div>
        </div>
        <div class="item-actions" v-if="item.id !== currentVersionId">
          <button
            class="rollback-btn"
            @click="$emit('rollback', item.id)"
          >
            回滚到此版本
          </button>
        </div>
      </div>

      <div v-if="history.length === 0" class="empty-state">
        暂无历史版本
      </div>
    </div>
  </div>
</template>

<style scoped>
.history-list {
  background-color: white;
  border-radius: 8px;
  padding: 16px;
  height: 100%;
}

.list-content {
  overflow-y: auto;
  max-height: 500px;
}

.history-item {
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  margin-bottom: 12px;
  transition: all 0.3s;
}

.history-item:hover {
  border-color: #d9d9d9;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.history-item.current {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.version {
  font-weight: bold;
  font-size: 16px;
}

.status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  background-color: #f5f5f5;
  color: #999;
}

.status.current {
  background-color: #e6f7ff;
  color: #1890ff;
}

.item-body {
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.label {
  color: #999;
  margin-right: 4px;
}

.item-actions {
  display: flex;
  justify-content: flex-end;
}

.rollback-btn {
  background-color: transparent;
  border: 1px solid #d9d9d9;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 12px;
}

.rollback-btn:hover {
  border-color: #1890ff;
  background-color: #f0f8ff;
}

.empty-state {
  text-align: center;
  padding: 24px;
  color: #999;
  font-style: italic;
}
</style>
