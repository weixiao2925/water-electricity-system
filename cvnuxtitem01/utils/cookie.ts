import type {H3Event} from "h3";

interface CookieOptions{
    expireDays?: number
    event?: H3Event
}

export function setCookie(
    key: string,
    value: string,
    options: CookieOptions = {}
):void{
    const saveValue:string = encodeAES(value)
    if (options.event){
        setServerCookie(
            options.event,
            key, saveValue,
            options.expireDays ? options.expireDays * 24 * 60 * 60 : undefined)
    }else {
        setClientCookie(key, saveValue, options)
    }
}

export function getCookie(
    key: string,
    event?: H3Event
):string | null{
    const rawValue: string | null | undefined = event
        ? getServerCookie(event, key)
        : getClientCookie(key)
    if (!rawValue) return null
    return decodeAES(rawValue)
}

export function removeCookie(key: string, event?: H3Event): void{
    if (event) {
        removeServerCookie(event, key)
    } else {
        removeClientCookie(key)
    }
}

export function removeAllCookies(event?: H3Event): void{
    const keys: string[] = [USER_TOKEN, USER_INFO_PREFIX]
    for (const key of keys){
        removeCookie(key, event)
    }
}
