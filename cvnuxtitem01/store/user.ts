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
    getters: {
        avatarUrl(): string {
            if (this.user.avatar){
                const { apiBase } = useRuntimeConfig().public
                return `${apiBase}/api/image${this.user.avatar}`
            } else
                return 'https://gss0.baidu.com/7Ls0a8Sm2Q5IlBGlnYG/sys/portrait/item/tb.1.2a112596.L6LokNyU4dYGPEGmLnRIrw';
            //http://localhost:8848/api/image/avatar/4aa54576fc914ee697380d43605c8f1f
        },
    },
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
        },
        avatarUserUrl(avatarUrl: string): string {
            if (avatarUrl) {
                const { apiBase } = useRuntimeConfig().public
                return `${apiBase}/api/image${avatarUrl}`
            }else
                return 'https://www.keaitupian.cn/cjpic/frombd/1/253/1215285637/1396751085.jpg';
        }
    }
})
