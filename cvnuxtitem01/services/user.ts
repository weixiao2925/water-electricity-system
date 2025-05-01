import {useNuxtApp} from "#app";
import type { UserInfo } from "~/types/home/profile/type";
export function useUserService(){
    const { $api } = useNuxtApp()
    return {
        apiUserInfo: async () =>{
            try {
                return await $api.get('/api/user/info')
            }catch (error){
                throw error
            }
        },
        apiUserInfoDetail: async () =>{
            try {
                return await $api.get('/api/user/info-detail')
            }catch (error){
                throw error
            }
        },
        apiUserInfoUpdate: async (data: UserInfo) =>{
            try {
                return await $api.post('/api/user/info-update', data)
            }catch (error){
                throw error
            }
        }
    }
}
