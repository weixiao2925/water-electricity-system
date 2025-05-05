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

    static Meter createMeter(DraftConsumer<MeterDraft> block) {
        return MeterDraft.$.produce(block);
    }

    static Meter createMeter(Meter base, DraftConsumer<MeterDraft> block) {
        return MeterDraft.$.produce(base, block);
    }

    static Reading createReading(DraftConsumer<ReadingDraft> block) {
        return ReadingDraft.$.produce(block);
    }

    static Reading createReading(Reading base, DraftConsumer<ReadingDraft> block) {
        return ReadingDraft.$.produce(base, block);
    }
}
