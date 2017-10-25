package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlModule;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.DtlFilterViewImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.start.DtlStartPath;

import butterknife.InjectView;
import flow.History;

@Layout(R.layout.activity_dtl)
public class DtlActivity extends FlowActivity<ActivityPresenter<ActivityPresenter.View>> {

   public static final int GPS_LOCATION_RESOLUTION_REQUEST = 481;

   @InjectView(R.id.drawer_filter_layout) DtlFilterViewImpl filterView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      navigationDrawerPresenter.setCurrentComponent(getCurrentComponent());
      setupFilter();
   }

   protected void setupFilter() {
      filterView.setInjector(this);
   }

   @Override
   protected ComponentDescription getCurrentComponent() {
      return rootComponentsProvider.getComponentByKey(DtlModule.DTL);
   }

   @Override
   protected History provideDefaultHistory() {
      return History.single(new DtlStartPath());
   }

   @Override
   protected ActivityPresenter<ActivityPresenter.View> createPresentationModel(Bundle savedInstanceState) {
      return new ActivityPresenter<>();
   }

   public static void startDtl(Context context) {
      context.startActivity(new Intent(context, DtlActivity.class));
   }
}
