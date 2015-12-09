package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

public class DtlMerchantsPredicate implements Predicate<DtlMerchant> {

    private DtlFilterData dtlFilterData;
    private LatLng currentLatLng;
    //
    private String query;
    //
    private DtlMerchantType merchantType;

    private DtlMerchantsPredicate(DtlFilterData dtlFilterData, LatLng currentLatLng, String query,
                                  DtlMerchantType merchantType) {
        this.dtlFilterData = dtlFilterData;
        this.currentLatLng = currentLatLng;
        this.query = query;
        this.merchantType = merchantType;
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return checkType(dtlMerchant)
                && checkFilter(dtlMerchant)
                && checkQuery(dtlMerchant);
    }

    private boolean checkType(DtlMerchant dtlMerchant) {
        return merchantType == null ||
                dtlMerchant.getMerchantType() == merchantType;
    }

    private boolean checkFilter(DtlMerchant dtlMerchant) {
        return dtlMerchant.applyFilter(dtlFilterData,
                currentLatLng);
    }

    private boolean checkQuery(DtlMerchant dtlMerchant) {
        return dtlMerchant.containsQuery(query);
    }

    public static class Builder {
        private DtlFilterData dtlFilterData;
        private LatLng currentLatLng;
        private String query;
        private DtlMerchantType merchantType;

        public static Builder create() {
            return new Builder();
        }

        public Builder withDtlFilterData(DtlFilterData dtlFilterData) {
            this.dtlFilterData = dtlFilterData;
            return this;
        }

        public Builder withLatLng(LatLng currentLatLng) {
            this.currentLatLng = currentLatLng;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withMerchantType(DtlMerchantType merchantType) {
            this.merchantType = merchantType;
            return this;
        }

        public DtlMerchantsPredicate build() {
            return new DtlMerchantsPredicate(dtlFilterData, currentLatLng, query, merchantType);
        }
    }

}
