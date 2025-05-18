export interface MonthlyBill {
    id: number;
    billMonth: string;
    meterWater: number;
    meterElectricity: number;
    meterGas: number;
    totalWater: number;
    totalElectricity: number;
    totalGas: number;
    totalCost: number;
    status: string;
    isPaid: boolean;
    paidDate: string;
}


