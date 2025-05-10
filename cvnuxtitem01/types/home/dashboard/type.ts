
export interface SumData {
    type: string,
    unit: string,
    current: number,
    cost: number
}

export interface RecentData {
    id: number,
    type: string,
    value: number,
    shotTime: string,
    cost: number
}

export const typeLabelMap: Record<string, string> = {
    water: "水表",
    electricity: "电表",
    gas: "气表"
}


