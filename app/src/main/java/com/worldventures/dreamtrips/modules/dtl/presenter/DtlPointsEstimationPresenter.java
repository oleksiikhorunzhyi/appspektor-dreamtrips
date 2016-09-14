package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimationHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.PointsEstimatorCalculateEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlPointsEstimationPresenter extends JobPresenter<DtlPointsEstimationPresenter.View> {

   public static final String BILL_TOTAL = "billTotal";
   private static final String NUMBER_REGEX = "[+-]?\\d*(\\.\\d+)?";

   protected final Merchant merchant;

   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject DtlMerchantInteractor merchantInteractor;

   public DtlPointsEstimationPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      view.showCurrency(MerchantHelper.merchantDefaultCurrency(merchant));
      bindApiJob();
   }

   private void bindApiJob() {
      transactionInteractor.estimatePointsActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<EstimationHttpAction>().onStart(action -> view.showProgress())
                  .onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(action -> view.showEstimatedPoints(action.estimatedPoints().points().intValue())));
   }

   public void onCalculateClicked(String userInput) {
      if (!validateInput(userInput)) return;
      //
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(new PointsEstimatorCalculateEvent(merchant)));
      //
      transactionInteractor.estimatePointsActionPipe()
            .send(new EstimationHttpAction(merchant.id(), ImmutableEstimationParams.builder()
                  .billTotal(Double.valueOf(userInput))
                  .checkinTime(DateTimeUtils.currentUtcString())
                  .currencyCode(MerchantHelper.merchantDefaultCurrency(merchant).code())
                  .build()));
   }

   protected boolean validateInput(String pointsInput) {
      if (pointsInput.isEmpty() || !pointsInput.matches(NUMBER_REGEX)) {
         view.showError(R.string.dtl_field_validation_empty_input_error);
         return false;
      }
      if (Double.valueOf(pointsInput) < 0D) {
         view.showError(R.string.dtl_points_estimation_negative_input_error);
         return false;
      }
      return true;
   }

   public interface View extends RxView, ApiErrorView {

      void showProgress();

      void showError(@StringRes int errorRes);

      void showEstimatedPoints(int value);

      void showCurrency(Currency currency);
   }
}
