<script setup lang="ts">
// 重定向到dashboard页面
onMounted(() => {
  navigateTo('/home');
});
</script>

<template>
  <div>
    <p>正在跳转到仪表盘...</p>
  </div>
</template>

<style scoped>
div {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 18px;
  color: #666;
}
</style>
