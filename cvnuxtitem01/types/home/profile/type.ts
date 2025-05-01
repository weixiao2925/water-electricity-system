export interface Detail{
    gender: number,
    phone: string,
    qq: string,
    wx: string,
    desc: string,
    address: string,
}
export interface UserInfo{
    id: number,
    username: string,
    email: string,
    avatar: string,
    registerTime: string,
    details: Detail,
}
export interface InfoForm{
    email: string,
}
export interface PasswordForm{
    oldPassword: string,
    newPassword: string,
    confirmPassword: string
}
export type ElFormInstance = InstanceType<typeof import('element-plus')['ElForm']>
