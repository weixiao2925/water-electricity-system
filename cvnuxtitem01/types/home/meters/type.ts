export interface Meter {
    id: number;
    type: 'water' | 'electricity' | 'gas';
    location: string;
    installDate: string | null;
}

export interface MeterSelf {
    meter: Meter;
    lastReading: number;
    unit: string;
    status: string;
}
