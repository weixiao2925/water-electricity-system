package org.example.cvitme01.entity.dto;

import org.babyfish.jimmer.DraftConsumer;
import org.babyfish.jimmer.internal.GeneratedBy;

@GeneratedBy
public interface Immutables {
    static Account createAccount(DraftConsumer<AccountDraft> block) {
        return AccountDraft.$.produce(block);
    }

    static Account createAccount(Account base, DraftConsumer<AccountDraft> block) {
        return AccountDraft.$.produce(base, block);
    }

    static AccountDetails createAccountDetails(DraftConsumer<AccountDetailsDraft> block) {
        return AccountDetailsDraft.$.produce(block);
    }

    static AccountDetails createAccountDetails(AccountDetails base,
            DraftConsumer<AccountDetailsDraft> block) {
        return AccountDetailsDraft.$.produce(base, block);
    }

    static Logs createLogs(DraftConsumer<LogsDraft> block) {
        return LogsDraft.$.produce(block);
    }

    static Logs createLogs(Logs base, DraftConsumer<LogsDraft> block) {
        return LogsDraft.$.produce(base, block);
    }

    static Meter createMeter(DraftConsumer<MeterDraft> block) {
        return MeterDraft.$.produce(block);
    }

    static Meter createMeter(Meter base, DraftConsumer<MeterDraft> block) {
        return MeterDraft.$.produce(base, block);
    }

    static MonthlyBillSummary createMonthlyBillSummary(
            DraftConsumer<MonthlyBillSummaryDraft> block) {
        return MonthlyBillSummaryDraft.$.produce(block);
    }

    static MonthlyBillSummary createMonthlyBillSummary(MonthlyBillSummary base,
            DraftConsumer<MonthlyBillSummaryDraft> block) {
        return MonthlyBillSummaryDraft.$.produce(base, block);
    }

    static Reading createReading(DraftConsumer<ReadingDraft> block) {
        return ReadingDraft.$.produce(block);
    }

    static Reading createReading(Reading base, DraftConsumer<ReadingDraft> block) {
        return ReadingDraft.$.produce(base, block);
    }

    static TariffTier createTariffTier(DraftConsumer<TariffTierDraft> block) {
        return TariffTierDraft.$.produce(block);
    }

    static TariffTier createTariffTier(TariffTier base, DraftConsumer<TariffTierDraft> block) {
        return TariffTierDraft.$.produce(base, block);
    }

    static TariffVersion createTariffVersion(DraftConsumer<TariffVersionDraft> block) {
        return TariffVersionDraft.$.produce(block);
    }

    static TariffVersion createTariffVersion(TariffVersion base,
            DraftConsumer<TariffVersionDraft> block) {
        return TariffVersionDraft.$.produce(base, block);
    }
}
