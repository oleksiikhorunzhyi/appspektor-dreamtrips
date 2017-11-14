package com.worldventures.dreamtrips.modules.dtl_flow.di;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.dreamtrips.R;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
            DtlMappingModule.class,
            DtlActionsModule.class,
            DtlDelegatesModule.class},
      complete = false, library = true)
public class DtlModule {

   public static final String DTL = "DTL";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideDtlComponent() {
      return new ComponentDescription.Builder()
            .key(DTL)
            .navMenuTitle(R.string.dtl_local)
            .toolbarTitle(R.string.dtl_local)
            .icon(R.drawable.ic_dtl)
            .skipGeneralToolbar(true)
            .shouldFinishMainActivity(true)
            .build();
   }

}
