package org.example.cvitme01.service.tariff.builder;

import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.example.cvitme01.entity.dto.*;
import org.springframework.stereotype.Component;

@Component
public class TariffFetcherBuilder {

    public static class TariffTierFetcherBuilder {
        private boolean includeSeq = false;
        private boolean includeUpperBound = false;
        private boolean includePrice = false;
        private boolean includeTariffVersion = false;

        public TariffTierFetcherBuilder withSeq() {
            this.includeSeq = true;
            return this;
        }

        public TariffTierFetcherBuilder withUpperBound() {
            this.includeUpperBound = true;
            return this;
        }

        public TariffTierFetcherBuilder withPrice() {
            this.includePrice = true;
            return this;
        }

        public TariffTierFetcherBuilder withTariffVersion() {
            this.includeTariffVersion = true;
            return this;
        }

        public Fetcher<TariffTier> build() {
            var fetcher = TariffTierFetcher.$;

            if (includeSeq) fetcher = fetcher.seq();
            if (includeUpperBound) fetcher = fetcher.upperBound();
            if (includePrice) fetcher = fetcher.price();
            if (includeTariffVersion) {
                fetcher = fetcher.tariffVersion(
                        TariffVersionFetcher.$
                                .type()
                                .version()
                                .startTime()
                                .endTime()
                                .isActive()
                );
            }

            return fetcher;
        }
    }

    public static class TariffVersionFetcherBuilder {
        private boolean includeVersion = false;
        private boolean includeStartTime = false;
        private boolean includeEndTime = false;
        private boolean includeType = false;
        private boolean includeIsActive = false;

        public TariffVersionFetcherBuilder withVersion() {
            this.includeVersion = true;
            return this;
        }

        public TariffVersionFetcherBuilder withStartTime() {
            this.includeStartTime = true;
            return this;
        }

        public TariffVersionFetcherBuilder withEndTime() {
            this.includeEndTime = true;
            return this;
        }

        public TariffVersionFetcherBuilder withType() {
            this.includeType = true;
            return this;
        }

        public TariffVersionFetcherBuilder withIsActive() {
            this.includeIsActive = true;
            return this;
        }

        public Fetcher<TariffVersion> build() {
            var fetcher = TariffVersionFetcher.$;

            if (includeVersion) fetcher = fetcher.version();
            if (includeStartTime) fetcher = fetcher.startTime();
            if (includeEndTime) fetcher = fetcher.endTime();
            if (includeType) fetcher = fetcher.type();
            if (includeIsActive) fetcher = fetcher.isActive();

            return fetcher;
        }
    }

    public TariffTierFetcherBuilder tariffTier() {
        return new TariffTierFetcherBuilder();
    }

    public TariffVersionFetcherBuilder tariffVersion() {
        return new TariffVersionFetcherBuilder();
    }
}
