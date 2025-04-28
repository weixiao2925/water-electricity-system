<template>
  <form @submit.prevent="$emit('save')">
    <div class="form-section">
      <h2 class="section-title">{{ title }}</h2>
      <slot></slot>
      <div class="form-actions">
        <button 
          v-if="showTestButton" 
          type="button" 
          class="test-btn" 
          @click="$emit('test')"
        >
          测试连接
        </button>
        <button type="submit" class="save-btn">保存配置</button>
      </div>
    </div>
  </form>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  showTestButton: {
    type: Boolean,
    default: false
  }
});

defineEmits(['save', 'test']);
</script>

<style scoped>
.section-title {
  font-size: 18px;
  margin-bottom: 24px;
  font-weight: 500;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.test-btn {
  padding: 10px 20px;
  background-color: #fff;
  border: 1px solid #1890ff;
  color: #1890ff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.test-btn:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.save-btn {
  padding: 10px 20px;
  background-color: #1890ff;
  border: 1px solid #1890ff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
</style>
