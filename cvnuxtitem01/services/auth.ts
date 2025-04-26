import { useNuxtApp } from "#app";
import {useAuthStore} from "~/store/auth";
import type {AuthToken} from "~/utils/token";

export function useAuthService() {
    const { $api } = useNuxtApp();
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
            const authStore = useAuthStore()
            // console.log(data)
            const access: AuthToken = {token: data.data.token, expire: data.data.expire, role: data.data.role}
            // console.log(access);
            authStore.updateToken(access, remember)
            ElMessage.success(`登录成功，欢迎${data.data.username}`)
            navigateTo('/')
            // console.log(authStore.accessToken)
            return data
        }

    }
}
