import { COOKIE_PREFIX} from "~/utils/constants";

const isClient:boolean = typeof window !== 'undefined'

interface CookieOptions {
    expireDays?: number      // 过期天数（可选）
}

function setClientCookie(
    key: string,
    value: string,
    options: CookieOptions = {}
){
    if (!isClient) return

    const fullKey = COOKIE_PREFIX + key
    let cookieStr: string = `${fullKey}=${value}`

    if (options.expireDays){
        const d = new Date();
        d.setTime(d.getTime() + options.expireDays * 24 * 60 * 60 * 1000);
        cookieStr += `;expires=${d.toUTCString()}`
    }

    cookieStr += `;path=/`
    document.cookie = cookieStr
}

function getClientCookie(key:string): string | null{
    if (!isClient) return null

    const fullKey = COOKIE_PREFIX + key
    const match = document.cookie.match(new RegExp('(?:^| )' + fullKey + '=([^;]+)'))
    return match ? match[1] : null
}

function removeClientCookie(key:string){
    if (!isClient) return

    const fullKey = COOKIE_PREFIX + key
    document.cookie = `${fullKey}=; Max-Age=0; path=/`
}

export { setClientCookie, getClientCookie, removeClientCookie }
