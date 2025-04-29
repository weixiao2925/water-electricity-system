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

function loadUserFromCookie(): UserInfo{
    const defaultUser: UserInfo = {
        id: null,
        username: '',
        email: '',
        role: '',
        avatar: '',
        registerTime: ''
    }
    try {
        const cookieValue = getCookie(USER_INFO_PREFIX)
        if (cookieValue){
            const parsedData = JSON.parse(cookieValue)
            return {
                ...defaultUser,
                ...parsedData,
                id: typeof parsedData.id === 'number' ? parsedData.id : defaultUser.id
            }
        }
    }catch(error){
        console.error(error)
    }

    return defaultUser
}

export const useUserStore = defineStore('user', {
    state:() => ({
        user: loadUserFromCookie()
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
        }
    }
})
