import {isUnauthorized} from "~/utils/token";

/**
 * 全局登录守卫：检查用户的登录状态
 *
 * @param {Object} to - 目标路由对象
 * @param {Object} from - 当前路由对象
 */
export default defineNuxtRouteMiddleware((to, from) => {

    const event = useRequestEvent()
    // 定义白名单路由，用户可以在未授权的情况下访问这些路由
    const whiteList = ['/welcome/login', '/welcome/register'];

    // 检查用户是否未授权且目标路由不在白名单中
    if (isUnauthorized(event)) {
        //未登录
        if (!whiteList.includes(to.path)) {
            return navigateTo(`/welcome/login?redirect=${to.fullPath}`);
        }
    }else {
        // 已登录
        if (whiteList.includes(to.path)) {
            return navigateTo('/');
        }
    }
});
