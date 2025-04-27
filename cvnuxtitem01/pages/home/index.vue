<script setup lang="ts">
const router = useRouter()
const route = useRoute()

onMounted(() => {
    if (route.path === '/home') {
        router.replace('/home/dashboard')
    }
})
</script>
