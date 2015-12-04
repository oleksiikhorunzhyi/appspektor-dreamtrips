package com.worldventures.dreamtrips.modules.dtl.api.place;

import com.worldventures.dreamtrips.modules.dtl.api.DtlRequest;
import com.worldventures.dreamtrips.modules.dtl.model.leads.DtlLead;

public class SuggestPlaceCommand extends DtlRequest<Void> {

    private final DtlLead leadData;

    public SuggestPlaceCommand(DtlLead leadData) {
        super(Void.class);
        this.leadData = leadData;
    }

    @Override
    public Void loadDataFromNetwork() {
        return getService().suggestPlace(leadData);
    }
}
