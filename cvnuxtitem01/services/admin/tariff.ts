import {TARIFF_TYPES} from "~/utils/constants";

export function useTariffService(){
    const { $api } = useNuxtApp()

    return {
        apiTariffList: async () =>{
            return await $api.get('api/admin/tariff/list')
        },
        apiTariffNowVersion: async (type: TARIFF_TYPES) =>{
            return await $api.get(`api/admin/tariff/now-version?type=${type}`)
        },
        apiTariffVersion: async (type: TARIFF_TYPES) =>{
            return await $api.get(`api/admin/tariff/version?type=${type}`)
        }
    }
}
