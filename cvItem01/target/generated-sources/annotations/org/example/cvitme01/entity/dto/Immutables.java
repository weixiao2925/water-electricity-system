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
}
