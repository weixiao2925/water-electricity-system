export interface LoginForm{
    username: string
    password: string
    remember_me: boolean
}
export type ElFormInstance = InstanceType<typeof import('element-plus')['ElForm']>
