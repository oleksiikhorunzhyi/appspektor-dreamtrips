package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateAmenitiesAction;

import dagger.Module;

@Module(
        injects = {
                DtlUpdateAmenitiesAction.class,
                DtlFilterDataAction.class,
                DtlTransactionAction.class
        },
        complete = false, library = true)
public class DtlJanetActionsModuile {
}
