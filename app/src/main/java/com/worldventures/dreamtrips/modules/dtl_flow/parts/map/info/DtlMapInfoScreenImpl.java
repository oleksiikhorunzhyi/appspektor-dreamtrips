package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantMapInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

public class DtlMapInfoScreenImpl extends DtlLayout<DtlMapInfoScreen, DtlMapInfoPresenter, DtlMapInfoPath> implements DtlMapInfoScreen {

   MerchantDataInflater commonDataInflater, categoryDataInflater;

   public DtlMapInfoScreenImpl(Context context) {
      super(context);
   }

   public DtlMapInfoScreenImpl(Context context, AttributeSet attributeSet) {
      super(context, attributeSet);
   }

   @Override
   public DtlMapInfoPresenter createPresenter() {
      return new DtlMapInfoPresenterImpl(getContext(), injector, getPath().getMerchant());
   }

   @Override
   protected void onPostAttachToWindowView() {
      commonDataInflater = new MerchantSingleImageDataInflater();
      categoryDataInflater = new MerchantMapInfoInflater();
      commonDataInflater.setView(this);
      categoryDataInflater.setView(this);
      observeSize(this);
      setOnClickListener(v -> getPresenter().onMarkerClick());
   }

   @Override
   protected void onDetachedFromWindow() {
      if (commonDataInflater != null) commonDataInflater.release();
      if (categoryDataInflater != null) categoryDataInflater.release();
      super.onDetachedFromWindow();
   }

   private void observeSize(final View view) {
      RxView.globalLayouts(view)
            .compose(RxLifecycle.bindView(view))
            .filter(aVoid -> view.getHeight() > 0)
            .take(1)
            .subscribe(aVoid -> getPresenter().onSizeReady(view.getHeight()));
   }

   @Override
   public void visibleLayout(boolean show) {
      setVisibility(show ? VISIBLE : GONE);
   }

   @Override
   public void setMerchant(DtlMerchant merchant) {
      commonDataInflater.applyMerchant(merchant);
      categoryDataInflater.applyMerchant(merchant);
   }
}
