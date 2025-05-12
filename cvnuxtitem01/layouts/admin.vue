<script setup lang="ts">
import {isRole} from "~/utils/role";
import {Role} from "~/utils/constants";
import {useAuthService} from "~/services/auth";
import {useUserStore} from "~/store/user";

interface MenuItem {
    path: string;
    icon: string;
    label: string;
}

const route = useRoute();
const store = useUserStore()

const isAdminPage = computed(() :boolean => route.fullPath.startsWith('/admin'));
const adminMenuItems: MenuItem[] = [
    { path: '/admin/overview', icon: '📊', label: '系统概览' },
    { path: '/admin/users', icon: '👥', label: '用户管理' },
    { path: '/admin/meters', icon: '🔧', label: '表计管理' },
    { path: '/admin/tariff', icon: '💰', label: '价格配置' },
    { path: '/admin/ocr-tasks', icon: '📋', label: '任务监控' },
    { path: '/admin/bills', icon: '📃', label: '账单中心' },
    { path: '/admin/log', icon: '📝', label: '操作/系统日志' },
    { path: '/admin/settrings', icon: '⚙️', label: '系统设置' }
];

const logout = ():void =>{
    const event = useRequestEvent()
    useAuthService()
        .logout(event)
}
const goTo = (where: string): void =>{
    navigateTo(where);
}

onMounted(()=>{
    if (!useRequestEvent()){
        store.initUserFromCookie()
    }
})

</script>

<template>
  <div class="app">
    <div class="sidebar">
        <div class="logo">
        <svg class="logo-svg" width="300" height="100" viewBox="0 0 800 200" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M377 200C379.16 200 381 198.209 381 196V103C381 103 386 112 395 127L434 194C435.785 197.74 439.744 200 443 200H470V50H443C441.202 50 439 51.4941 439 54V148L421 116L385 55C383.248 51.8912 379.479 50 376 50H350V200H377Z" fill="currentColor"></path>
          <path d="M726 92H739C742.314 92 745 89.3137 745 86V60H773V92H800V116H773V159C773 169.5 778.057 174 787 174H800V200H783C759.948 200 745 185.071 745 160V116H726V92Z" fill="currentColor"></path>
          <path d="M591 92V154C591 168.004 585.742 179.809 578 188C570.258 196.191 559.566 200 545 200C530.434 200 518.742 196.191 511 188C503.389 179.809 498 168.004 498 154V92H514C517.412 92 520.769 92.622 523 95C525.231 97.2459 526 98.5652 526 102V154C526 162.059 526.457 167.037 530 171C533.543 174.831 537.914 176 545 176C552.217 176 555.457 174.831 559 171C562.543 167.037 563 162.059 563 154V102C563 98.5652 563.769 96.378 566 94C567.96 91.9107 570.028 91.9599 573 92C573.411 92.0055 574.586 92 575 92H591Z" fill="currentColor"></path>
          <path d="M676 144L710 92H684C680.723 92 677.812 93.1758 676 96L660 120L645 97C643.188 94.1758 639.277 92 636 92H611L645 143L608 200H634C637.25 200 640.182 196.787 642 194L660 167L679 195C680.818 197.787 683.75 200 687 200H713L676 144Z" fill="currentColor"></path>
          <path d="M168 200H279C282.542 200 285.932 198.756 289 197C292.068 195.244 295.23 193.041 297 190C298.77 186.959 300.002 183.51 300 179.999C299.998 176.488 298.773 173.04 297 170.001L222 41C220.23 37.96 218.067 35.7552 215 34C211.933 32.2448 207.542 31 204 31C200.458 31 197.067 32.2448 194 34C190.933 35.7552 188.77 37.96 187 41L168 74L130 9.99764C128.228 6.95784 126.068 3.75491 123 2C119.932 0.245087 116.542 0 113 0C109.458 0 106.068 0.245087 103 2C99.9323 3.75491 96.7717 6.95784 95 9.99764L2 170.001C0.226979 173.04 0.00154312 176.488 1.90993e-06 179.999C-0.0015393 183.51 0.229648 186.959 2 190C3.77035 193.04 6.93245 195.244 10 197C13.0675 198.756 16.4578 200 20 200H90C117.737 200 137.925 187.558 152 164L186 105L204 74L259 168H186L168 200ZM89 168H40L113 42L150 105L125.491 147.725C116.144 163.01 105.488 168 89 168Z" fill="#00DC82"></path>
        </svg>
        <h1>水电气表识读后台管理</h1>
        </div>
        <nav class="menu">
            <NuxtLink
                v-for="item in adminMenuItems"
                :key="item.path"
                :to="item.path"
                class="menu-item"
                active-class="router-link-active"
            >
                <span class="icon">{{ item.icon }}</span>
                <span class="menu-label">{{ item.label }}</span>
            </NuxtLink>
        </nav>
    </div>
    <div class="content">
      <header class="header">
        <div class="search">
          <input type="text" placeholder="搜索..." />
        </div>
        <div class="user">
          <template v-if="isRole(Role.Admin)">
            <el-button type="primary" size="small"
                      v-if="isAdminPage"
                      @click="goTo('/home')">
              回到客户端
              <el-icon style="margin-left: 5px;">
                <ElIconRight/>
              </el-icon>
            </el-button>
          </template>
          <span class="user-name">{{ store.user.username }}</span>
          <el-dropdown>
            <div>
                <el-avatar size="default" :src="store.avatarUrl" />
            </div>
            <template #dropdown>
              <el-dropdown-item @click="goTo('/home/profile')">
                <el-icon><ElIconHouse/></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item divided @click="logout">
                <el-icon><ElIconBack/></el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="main-content">
        <slot />
      </main>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 250px;
  min-width: 250px; /* 添加最小宽度，确保不会收缩 */
  background-color: #2c3e50;
  color: white;
  flex-shrink: 0; /* 防止侧边栏被压缩 */
}

.logo {
  padding: 15px 20px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.logo-svg {
  -webkit-font-smoothing: antialiased;
  color-scheme: dark;
  --tw-backdrop-blur: blur(8px);
  pointer-events: auto;
  fill: none;
  border: 0 solid;
  box-sizing: border-box;
  padding: 0;
  display: block;
  height: 2rem;
  width: auto;
  color: var(--ui-text-highlighted, white);
  --ui-color-primary-400: #00dc82;
  --ui-text-highlighted: white;
  margin-bottom: 15px;
}

.logo h1 {
  font-size: 18px;
  font-weight: 500;
  text-align: center;
  line-height: 1.2;
  letter-spacing: 0.5px;
  margin-top: 5px;
}

.menu {
  padding: 20px 0;
  width: 100%; /* 确保菜单占满侧边栏宽度 */
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: #ddd;
  text-decoration: none;
  transition: background-color 0.3s;
  width: 100%;
  white-space: nowrap; /* 防止文本换行 */
}

.menu-item:hover, .menu-item.router-link-active {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.icon {
  margin-right: 10px;
  font-size: 20px;
  flex-shrink: 0; /* 防止图标被压缩 */
}

.menu-label {
  white-space: nowrap; /* 防止文本换行 */
  overflow: hidden; /* 处理文本溢出 */
  text-overflow: ellipsis; /* 文本溢出时显示省略号 */
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
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
  gap: 5px;
}

.user-name {
  margin-right: 10px;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
