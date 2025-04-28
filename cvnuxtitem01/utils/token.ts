import { TokenKey } from '~/utils/constants'
        import type { H3Event } from 'h3'

        export interface AuthToken {
            token: string
            expire: string
            role: string
        }

        export const storeAccessToken = (
            token: string,
            remember: boolean,
            expire: string | number | Date,
            role: string
        ): void => {
            // 处理不同格式的expire
            let expireDate: Date;

            if (expire instanceof Date) {
                expireDate = expire;
            } else if (typeof expire === 'number') {
                expireDate = new Date(expire);
            } else {
                { // 尝试解析格式为 "YYYY-MM-DD HH:mm:ss.SSS" 的字符串
                    if (expire.includes(' ')) {
                        const [datePart, timePart] = expire.split(' ');
                        const [year, month, day] = datePart.split('-').map(Number);
                        const [hours, minutes, secondsWithMs] = timePart.split(':').map(val => {
                            // 处理可能包含毫秒的秒数部分
                            if (val.includes('.')) {
                                return parseFloat(val);
                            }
                            return parseInt(val);
                        });

                        const seconds = Math.floor(secondsWithMs || 0);
                        const ms = secondsWithMs ? Math.round((secondsWithMs - seconds) * 1000) : 0;

                        // 注意：月份是从0开始的
                        expireDate = new Date(year, month - 1, day, hours, minutes, seconds, ms);
                    } else {
                        // 尝试标准格式解析
                        expireDate = new Date(expire);
                    }
                }
            }

            // 检查日期是否有效
            if (isNaN(expireDate.getTime())) {
                console.error('无效的过期时间格式', expire);
                expireDate = new Date(Date.now() + 24 * 60 * 60 * 1000); // 默认一天
            }

            const expireStr: string = expireDate.toISOString();
            const authObj: AuthToken = { token, expire: expireStr, role };
            const str: string = JSON.stringify(authObj);

            const expireDays: number | undefined = remember ? 7 : undefined;
            setCookie(TokenKey.Access, str, { expireDays });
        }

        export const takeAccessToken = (event?: H3Event): AuthToken | null => {
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
                return authObj
            }catch (e){
                console.error(e)
                delAccessToken(event)
                return null
            }
        }

        export const delAccessToken = (event?: H3Event):void =>{
            removeCookie(TokenKey.Access, event)
        }

        export const accessHeader = (event?: H3Event):Record<string, string> => {
            const auth :AuthToken | null = takeAccessToken(event)
            return auth ? { 'Authorization': `Bearer ${auth.token}` } : {};
        }

        export const isUnauthorized = (event?: H3Event): boolean =>
            !takeAccessToken(event)
