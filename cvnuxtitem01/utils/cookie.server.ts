import { COOKIE_PREFIX} from "~/utils/constants";
import { getCookie as h3GetCookie, setCookie as h3SetCookie, deleteCookie as h3DeleteCookie } from 'h3'
import type { H3Event } from 'h3'

function setServerCookie(
    client: H3Event,
    key: string,
    value: string ,
    maxAgeSeconds?: number
):void{
    const fullKey: string = COOKIE_PREFIX + key
    h3SetCookie(client, fullKey, value, {
        path: '/',
        maxAge: maxAgeSeconds || undefined
    })
}

function getServerCookie(event: H3Event, key: string): string | undefined{
    const fullKey: string = COOKIE_PREFIX + key
    return h3GetCookie(event, fullKey)
}

function removeServerCookie(event: H3Event, key: string): void{
    const fullKey: string = COOKIE_PREFIX + key
    h3DeleteCookie(event, fullKey, {path: '/'})
}

export { setServerCookie, getServerCookie, removeServerCookie }
