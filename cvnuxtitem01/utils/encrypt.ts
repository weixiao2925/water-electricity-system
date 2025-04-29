/**
 * 通用加密工具
 */
import CryptoJS from 'crypto-js'


// AES加密
export function encodeAES(str: string): string {
    return CryptoJS.AES.encrypt(str, AES_SECRET_KEY).toString()
}

// AES解密
export function decodeAES(str: string): string {
    try {
        const bytes = CryptoJS.AES.decrypt(str, AES_SECRET_KEY)
        return bytes.toString(CryptoJS.enc.Utf8)
    } catch (e) {
        console.error('AES解密失败', e)
        return ''
    }
}
