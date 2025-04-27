<script setup lang="ts">
const router = useRouter()
const route = useRoute()

interface MenuItem {
    path: string;
    icon: string;
    label: string;
}

// 定义菜单项数组
const menuItems: MenuItem[] = [
    { path: '/home/dashboard', icon: '📊', label: '仪表盘' },
    { path: '/home/upload', icon: '📷', label: '上传识读' },
    { path: '/home/meters', icon: '🔧', label: '我的表计' },
    { path: '/home/bills', icon: '📃', label: '账单中心' }
];

onMounted(() => {
    if (route.path === '/home') {
        router.replace('/home/dashboard')
    }
})
</script>

<template>
    <div class="app">
        <div class="sidebar">
            <div class="logo">
                <h1>水电气表识读系统</h1>
            </div>
            <nav class="menu">
                <NuxtLink
                    v-for="item in menuItems"
                    :key="item.path"
                    :to="item.path"
                    class="menu-item"
                    active-class="router-link-active"
                >
                    <span class="icon">{{ item.icon }}</span>
                    <span>{{ item.label }}</span>
                </NuxtLink>
            </nav>
        </div>
        <div class="content">
            <header class="header">
                <div class="search">
                    <input type="text" placeholder="搜索..." />
                </div>
                <div class="user">
                    <span class="user-name">用户名</span>
                    <div class="avatar">👤</div>
                </div>
            </header>
            <main class="main-content">
                <slot />
            </main>
        </div>
    </div>
</template>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Arial', sans-serif;
    background-color: #f5f7fa;
    color: #333;
}

.app {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: 250px;
    background-color: #2c3e50;
    color: white;
    padding: 20px 0;
}

.logo {
    padding: 0 20px 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h1 {
    font-size: 18px;
    text-align: center;
}

.menu {
    padding: 20px 0;
}

.menu-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    color: #ddd;
    text-decoration: none;
    transition: background-color 0.3s;
}

.menu-item:hover, .menu-item.router-link-active {
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
}

.icon {
    margin-right: 10px;
    font-size: 20px;
}

.content {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 30px;
    background-color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search input {
    padding: 8px 15px;
    border: 1px solid #ddd;
    border-radius: 20px;
    width: 250px;
}

.user {
    display: flex;
    align-items: center;
}

.user-name {
    margin-right: 10px;
}

.avatar {
    width: 35px;
    height: 35px;
    border-radius: 50%;
    background-color: #eee;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
}

.main-content {
    flex: 1;
    padding: 20px;
}
</style>
