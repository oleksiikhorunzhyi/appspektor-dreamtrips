package com.worldventures.dreamtrips.modules.dtl_flow.di;

import dagger.Module;

@Module(
      includes = {
            DtlMappingModule.class,
            DtlActionsModule.class,
            DtlDelegatesModule.class},
      complete = false, library = true)
public class DtlModule {
}
