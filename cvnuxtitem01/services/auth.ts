import { useNuxtApp } from "#app";
import {useAuthStore} from "~/store/auth";
import type {LocationQueryValue} from "#vue-router";
import type {H3Event} from "h3";

export function useAuthService() {
    const { $api } = useNuxtApp();
    const authStore = useAuthStore()
    return {
        login: async (username: string, password: string, remember: boolean) =>{
            const formData = new URLSearchParams();
            formData.append('username', username);
            formData.append('password', password);
            formData.append('remember', remember.toString())
            const data = await $api.post('/api/auth/login',
                formData,
                {headers: { 'Content-Type': 'application/x-www-form-urlencoded' }}
            )
            const route = useRoute()
            // console.log(data)
            const access: AuthToken = {token: data.data.token, expire: data.data.expire, role: data.data.role}
            // console.log(access);
            authStore.updateToken(access, remember)
            ElMessage.success(`登录成功，欢迎${data.data.username}`)
            const redirect: string | LocationQueryValue[] = route.query.redirect || '/'
            navigateTo(redirect as string)
            // console.log(authStore.accessToken)
            return data
        },
        logout: async (event?: H3Event) => {
            try {
                await $api.post('/api/auth/logout')
                authStore.clear(event)
                navigateTo('/welcome')
                ElMessage.success('退出登录成功，欢迎再次使用');
            }catch(error){
                throw error
            }

        }
    }
}
