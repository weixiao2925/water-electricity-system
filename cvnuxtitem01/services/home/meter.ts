
export function useHomeMeterService() {
    const { $api } = useNuxtApp();

    return {
        apiGetMeterSelf: async (type: "water" | "electricity" | "gas" | "") => {
            return await $api.get(`/api/home/meters/meter-self?type=${type}`);
        },
        apiGetMererList: async (meterId: number) => {
            return await $api.get(`/api/home/meters/meter-list?meterId=${meterId}`);
        }
    };
}
