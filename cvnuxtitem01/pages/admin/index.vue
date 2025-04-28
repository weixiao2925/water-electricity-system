<script setup lang="ts">
const router = useRouter()
const route = useRoute()

onMounted(() => {
    if (route.path === '/admin') {
        router.replace('/admin/overview')
    }
})
</script>

<template>

</template>

<style scoped>

</style>
