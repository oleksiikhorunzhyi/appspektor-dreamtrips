package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.AMENITIES_UPDATE;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.APPLY_PARAMS;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.APPLY_SEARCH;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.INIT;
import static com.worldventures.dreamtrips.modules.dtl.action.DtlFilterMerchantStoreAction.Action.RESET;

@CommandAction
public class DtlFilterMerchantStoreAction extends ValueCommandAction<DtlFilterMerchantStoreAction.Action> {

    private final DtlFilterParameters filterParameters;
    private final List<DtlMerchantAttribute> amenities;
    private final String query;

    public enum Action {
        INIT, RESET, AMENITIES_UPDATE, APPLY_PARAMS, APPLY_SEARCH
    }

    private DtlFilterMerchantStoreAction(Action value, DtlFilterParameters filterParameters,
                                         List<DtlMerchantAttribute> amenities, String query) {
        super(value);
        this.filterParameters = filterParameters;
        this.amenities = amenities;
        this.query = query;
    }

    public DtlFilterParameters getFilterParameters() {
        return filterParameters;
    }

    public List<DtlMerchantAttribute> getAmenities() {
        return amenities;
    }

    public String getQuery() {
        return query;
    }

    public static DtlFilterMerchantStoreAction init() {
        return new DtlFilterMerchantStoreAction(INIT, null, null, null);
    }

    public static DtlFilterMerchantStoreAction reset() {
        return new DtlFilterMerchantStoreAction(RESET, null, null, null);
    }

    public static DtlFilterMerchantStoreAction amenitiesUpdate(List<DtlMerchantAttribute> amenities) {
        return new DtlFilterMerchantStoreAction(AMENITIES_UPDATE, null, amenities, null);
    }

    public static DtlFilterMerchantStoreAction applyParams(DtlFilterParameters filterParameters) {
        return new DtlFilterMerchantStoreAction(APPLY_PARAMS, filterParameters, null, null);
    }

    public static DtlFilterMerchantStoreAction applySearch(String query) {
        return new DtlFilterMerchantStoreAction(APPLY_SEARCH, null, null, query);
    }
}
