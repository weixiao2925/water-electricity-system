import {useNuxtApp} from "#app";

export function useUserService(){
    const { $api } = useNuxtApp()
    return {
        apiUserInfo: async () =>{
            try {
                return await $api.get('/api/user/info')
            }catch (error){
                throw error
            }
        }
    }
}
