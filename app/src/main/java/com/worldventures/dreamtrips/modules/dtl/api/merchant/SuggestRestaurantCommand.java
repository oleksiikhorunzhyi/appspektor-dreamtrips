package com.worldventures.dreamtrips.modules.dtl.api.merchant;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;

public class SuggestRestaurantCommand extends DtlRequest<Void> {

    private final DtlLead leadData;

    public SuggestRestaurantCommand(DtlLead leadData) {
        super(Void.class);
        this.leadData = leadData;
    }

    @Override
    public Void loadDataFromNetwork() {
        return getService().suggestLead(leadData);
    }
}
