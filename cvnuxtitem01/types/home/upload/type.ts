export interface UploadParams {
    file: File;
    type: 'water' | 'electricity' | 'gas';
}

export interface Meter {
    type: 'water' | 'electricity' | 'gas';
    location: string | null;
}

export interface Reading {
    value: number | null;
    shotTime: string | null;
    imageUrl: string | null;
    cost: number | null;
    meter: Meter
}
