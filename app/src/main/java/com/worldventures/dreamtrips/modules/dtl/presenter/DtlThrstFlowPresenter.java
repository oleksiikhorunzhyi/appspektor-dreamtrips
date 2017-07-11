package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;

public class DtlThrstFlowPresenter extends JobPresenter<DtlThrstFlowPresenter.View> {

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public interface View extends RxView, ApiErrorView {

   }
}
