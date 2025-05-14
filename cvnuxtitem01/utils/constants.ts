/**
 * 系统常量定义
 */
// 加密相关
export const AES_SECRET_KEY:string = 'abcde1234'

// Cookie 相关
export const COOKIE_PREFIX:string = 'myapp_' // cookie 前缀
export const USER_INFO_PREFIX:string = 'user_info_'
export const USER_TOKEN:string = 'access_token'
export const USER_REMEMBER:string = 'remember_me:'

// 角色相关
export enum Role{
    Admin = 'admin',
    User = 'user',
}

// 类型相关
export enum TARIFF_TYPES{
    Water = 'water',
    Electricity = 'electricity',
    Gas = 'gas',
}
