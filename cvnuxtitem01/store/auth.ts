import {defineStore} from "pinia";

export const useAuthStore = defineStore('auth', {
    state: ()=>({
        accessToken: getCookie(TokenKey.Access) as AuthToken | null
    }),
    actions: {
        updateToken(access: AuthToken, remember: boolean): void {
            this.accessToken = access
            storeAccessToken(access.token, remember, access.expire, access.role)
        },
        clear(){
            this.accessToken = null
            delAccessToken()
        }
    }
})
