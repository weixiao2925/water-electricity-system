
export function useHomeMeterService() {
    const { $api } = useNuxtApp();

    return {
        apiGetMeterList: async (type: "water" | "electricity" | "gas" | "") => {
            return await $api.get(`/api/home/meters/meter-list?type=${type}`);
        },
    };
}
