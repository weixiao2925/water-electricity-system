import { TokenKey } from '~/utils/constants'
import type { H3Event } from 'h3'

export interface AuthToken {
    token: string
    expire: string
    role: string
}

let tokenCache :AuthToken | null = null

export const storeAccessToken = (
    token:string,
    remember:boolean,
    expire:string | number | Date,
    role:string
):void =>{
    const expireStr:string = new Date(expire).toISOString()

    const authObj:AuthToken = {token, expire: expireStr, role}
    const str:string = JSON.stringify(authObj)

    const expireDays:number | undefined = remember ? 7 : undefined
    setCookie(TokenKey.Access, str, {expireDays})

    tokenCache = authObj
}

export const takeAccessToken = (event?: H3Event): AuthToken | null => {
    if (tokenCache) return tokenCache

    let tokenStr:string | null = getCookie(TokenKey.Access, event)

    if (!tokenStr) return null

    try {
        const authObj = JSON.parse(tokenStr) as AuthToken
        if (!authObj.token || !authObj.expire || !authObj.role) {
            console.error('Token格式不正确');
            return null
        }

        if (new Date(authObj.expire) <= new Date()){
            delAccessToken()
            return null
        }
        tokenCache = authObj
        return authObj
    }catch (e){
        console.error(e)
        delAccessToken(event)
        return null
    }
}

export const delAccessToken = (event?: H3Event):void =>{
    tokenCache = null
    removeCookie(TokenKey.Access, event)
}

export const accessHeader = (event?: H3Event):Record<string, string> => {
    const auth :AuthToken | null = takeAccessToken(event)
    return auth  ? { 'Authorization': `Bearer ${auth .token}` } : {};
}

export const isUnauthorized = ():boolean => !takeAccessToken()

