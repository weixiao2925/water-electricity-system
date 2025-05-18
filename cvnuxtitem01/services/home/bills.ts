
export function useHomeBillsService() {
    const { $api } = useNuxtApp();

    return {
        apiGetBillsSummary: async (year: string) => {
            return await $api.get(`/api/home/bill/summary?year=${year}`);
        },
        apiSuccess: async (id: number) => {
            return await $api.post(`/api/home/bill/success?id=${id}`);
        }
    };
}
