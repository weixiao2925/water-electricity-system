export interface Meter {
    id: number;
    type: 'water' | 'electricity' | 'gas';
    location: string;
    installDate: string | null;
    reading: Reading;
}

export interface MeterHome {
    meter: Meter;
    lastReading: number;
    unit: string;
    status: string;
}

export interface Reading {
    id: number;
    shotTime: string;
    value: number;
    cost: number;
}
