import type {TariffTierSaveRequest, TariffVersion, VersionChange} from "~/types/admin/tariff/type";

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
        },
        apiTariffChangeVersion: async (data: VersionChange) => {
            const formData = new FormData()
            formData.append("type", data.type)
            formData.append("oldId", data.oldId.toString())
            formData.append("newId", data.newId.toString())
            return await $api.post('api/admin/tariff/version-change', formData)
        },
        apiTariffSave: async (type: string, versionId: number, tiers: TariffTierSaveRequest) => {
            return await $api.post(`api/admin/tariff/save?type=${type}&versionId=${versionId}`, tiers)
        },
        apiTariffAddVersion: async (data: TariffVersion) => {
            return await $api.post(`api/admin/tariff/version-add`, data)
        }
    }
}
