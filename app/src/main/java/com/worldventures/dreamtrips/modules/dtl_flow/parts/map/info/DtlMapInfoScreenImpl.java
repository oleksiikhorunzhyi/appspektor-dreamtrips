package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantMapInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DtlMapInfoScreenImpl extends DtlLayout<DtlMapInfoScreen, DtlMapInfoPresenter, DtlMapInfoPath> implements DtlMapInfoScreen {

   MerchantDataInflater commonDataInflater, categoryDataInflater;
   SweetAlertDialog errorDialog;

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
      categoryDataInflater = new MerchantMapInfoInflater(injector);
      categoryDataInflater.registerRatingsClickListener(() -> getPresenter().onClickRatingsReview());
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
   public void setMerchant(ThinMerchant merchant) {
      commonDataInflater.applyMerchantAttributes(merchant.asMerchantAttributes());
      categoryDataInflater.applyMerchantAttributes(merchant.asMerchantAttributes());
   }

   @OnClick(R.id.layout_rating_reviews_map)
   public void onClickRatingReviews() {
      if (getPresenter().hasPendingReview()) {
         errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
         errorDialog.setTitleText(getActivity().getString(R.string.app_name));
         errorDialog.setContentText(getContext().getString(R.string.text_awaiting_approval_review));
         errorDialog.setConfirmText(getActivity().getString(R.string.apptentive_ok));
         errorDialog.showCancelButton(true);
         errorDialog.setConfirmClickListener(listener -> listener.dismissWithAnimation());
         errorDialog.show();
      } else {
         categoryDataInflater.notifyRatingsClickListeners();
      }
   }
}
