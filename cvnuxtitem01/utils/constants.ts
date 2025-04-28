/**
 * 系统常量定义
 */
// 加密相关
export const AES_SECRET_KEY:string = 'abcde1234'

// Cookie 相关
export const COOKIE_PREFIX:string = 'myapp_'


// Token 相关
export enum TokenKey {
    Access = 'access_token',
}

// 角色相关
export enum Role{
    Admin = 'admin',
    User = 'user',
}
