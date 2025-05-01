// import { defineStore } from "pinia";
import type {H3Event} from "h3";

interface UserInfo{
    id: number | null
    username: string
    email: string
    role: string
    avatar: string
    registerTime: string
}
interface CookieOptions{
    expireDays?: number
    event?: H3Event
}

export const useUserStore = defineStore('user', {
    state:() => ({
        user: {
            id: null,
            username: '',
            email: '',
            role: '',
            avatar: '',
            registerTime: '',
        } as UserInfo
    }),
    actions: {
        setUser(userData: UserInfo, options: CookieOptions = {}): void {
            this.user = userData
            try {
                setCookie(USER_INFO_PREFIX, JSON.stringify(userData), options)
            }catch(error){
                console.error(error)
            }
        },
        clearUser(): void {
            this.user = {
                id: null,
                username: '',
                email: '',
                role: '',
                avatar: '',
                registerTime: '',
            };
            // 同时清除 Cookie
            removeCookie(USER_INFO_PREFIX);
        },
        initUserFromCookie() {
            try {
                const cookieData = getCookie(USER_INFO_PREFIX);
                if (cookieData) {
                    const parsedData = JSON.parse(cookieData);
                    if (parsedData && typeof parsedData === 'object') {
                        this.user = { ...this.user, ...parsedData };
                    }
                }
            } catch (e) {
                console.error('解析用户信息失败', e);
            }
        }
    }
})
