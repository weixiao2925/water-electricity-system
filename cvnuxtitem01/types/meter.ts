export interface Meter {
  id: number;
  meterNumber: string;
  address: string;
  owner: string;
  installDate: string;
  active: boolean;
  createdAt: string;
}

export interface MeterImport {
  meterNumber: string;
  address: string;
  owner: string;
  installDate: string;
}

export interface ImportResult {
  success: number;
  failed: number;
}
