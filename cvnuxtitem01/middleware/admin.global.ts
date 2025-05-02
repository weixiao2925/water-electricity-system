export default defineNuxtRouteMiddleware((to) => {
    if (to.path.startsWith('/admin')) {
        // 检查用户是否有管理员权限
        if (!isRole(Role.Admin)){
            // 如果用户不是管理员，重定向到首页
            return navigateTo('/home');
        }
    }
})
