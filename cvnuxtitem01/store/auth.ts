import {defineStore} from "pinia";
import {H3Event} from "h3";
import {takeAccessToken} from "~/utils/token";

export const useAuthStore = defineStore('auth', {
    state: ()=>({
        accessToken: takeAccessToken(useRequestEvent()) as AuthToken | null
    }),
    actions: {
        updateToken(access: AuthToken, remember: boolean): void {
            this.accessToken = access
            storeAccessToken(access.token, remember, access.expire, access.role)
        },
        clear(event?: H3Event){
            this.accessToken = null
            delAccessToken(event)
        }
    }
})
