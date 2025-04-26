import {createFetch, FetchError} from "ofetch";
import {useAuthStore} from "~/store/auth";
import defu from "defu";

export default defineNuxtPlugin((nuxtApp)=>{
    const { apiBase } = useRuntimeConfig().public
    const auth = useAuthStore()

    // ------------ 定制版 $fetch -------------
    const api = createFetch({
        defaults:{
            baseURL:apiBase,
            onRequest({ options }):void{
               if (auth.accessToken){
                   options.headers = defu(options.headers, {
                          Authorization: `Bearer ${auth.accessToken.token}`
                   })
               }
            },

            onRequestError({ error}):void{
                console.error('请求错误', error)
                throw error
            },

            onResponse({ response }):void{

            },

            onResponseError({ response }){
                console.log(response)

                if (response.status === 401){
                    auth.clear()
                    navigateTo('/welcome/login')
                    ElMessage.warning('登录过期，请重新登录')
                }

                switch (response.status){
                    case 400:
                        ElMessage.warning('请求参数错误');
                        break
                    case 404:
                        ElMessage.warning('请求的资源不存在');
                        showError({ statusCode: 404, statusMessage: '请求的资源不存在' });
                        break
                    case 500:
                        showError({ statusCode: 500, statusMessage: '服务器异常' });
                        break
                    default:
                        throw new FetchError(response._data || '未知错误,请联系管理员')
                }
            }

        },
    })

    const build =
        <T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE') =>
            <R = T, B = unknown>(url: string, body?: B, opts: any = {}) =>
                api<R>(url, defu(opts, {method, body}))

    const apiHelpers = {
        get : build('GET'),
        post: build('POST'),
        put: build('PUT'),
        delete: build('DELETE'),
    }

    nuxtApp.provide('api', apiHelpers)
    nuxtApp.provide('apiRaw', api)
})
