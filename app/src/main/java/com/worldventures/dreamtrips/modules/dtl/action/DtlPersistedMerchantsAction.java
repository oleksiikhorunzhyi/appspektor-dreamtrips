package com.worldventures.dreamtrips.modules.dtl.action;

import com.worldventures.dreamtrips.core.api.action.CallableCommandAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlPersistedMerchantsAction extends CallableCommandAction<List<DtlMerchant>> {

    public DtlPersistedMerchantsAction(SnappyRepository db) {
        super(db::getDtlMerchants);
    }
}
