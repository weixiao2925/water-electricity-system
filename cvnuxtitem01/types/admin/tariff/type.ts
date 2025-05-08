
export interface TariffItem {
    id: number;
    seq: number;
    upperBound: number | null;
    price: number;
    tariffVersion: {
        type: 'water' | 'electricity' | 'gas';
        version: string;
        startTime: string;
        endTime: string;
        isActive: boolean;
    }
}

export interface Version {
    id: number;
    version: string;
    startTime: string;
}

export interface VersionChange {
    type: 'water' | 'electricity' | 'gas';
    oldId: number,
    newId: number;
}

export type GroupedTariff = {
    [type in TariffItem['tariffVersion']['type']] ?: {
        [version: string]: TariffItem[]
    }
}

export function groupByTypeAudVersion(data: TariffItem[]): GroupedTariff {
    const result: GroupedTariff = {};

    for (const item of data) {
        const { type, version } = item.tariffVersion;

        if (!result[type]) {
            result[type] = {};
        }

        if (!result[type]![version]) {
            result[type]![version] = [];
        }

        result[type]![version].push(item);
    }

    return result;
}
