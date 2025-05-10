

export function useDashboardService(){
    const { $api } = useNuxtApp()

    return {
        apiSum: async () =>{
            try {
                return await $api.get('/api/home/dashboard/sum')
            }catch (error){
                throw error
            }
        },
        apiRecentData: async () =>{
            try {
                return await $api.get('/api/home/dashboard/recent')
            }catch (error){
                throw error
            }
        }
    }
}
