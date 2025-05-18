
export function useHomeBillsService() {
    const { $api } = useNuxtApp();

    return {
        apiGetBillsSummary: async (year: string) => {
            return await $api.get(`/api/home/bill/summary?year=${year}`);
        },
    };
}
